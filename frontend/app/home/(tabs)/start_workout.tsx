import apiClient from "@/app/services/apiClient";
import { useFocusEffect, useRouter } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Image,
  KeyboardAvoidingView,
  Modal,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

interface WorkoutSet {
  id: number;
  setNumber: number;
  plannedReps?: number;
  plannedWeight?: number;
  actualReps?: number;
  actualWeight?: number;
  completed: boolean;
}

interface WorkoutExercise {
  id: number;
  exerciseName: string;
  imageUrl?: string;
  orderIndex: number;
  sets: WorkoutSet[];
}

interface WorkoutDetail {
  id: number;
  templateName: string;
  status: string;
  startedAt: string;
  finishedAt?: string;
  exercises: WorkoutExercise[];
}

export default function StartWorkoutTab() {
  const router = useRouter();

  const [workout, setWorkout] = useState<WorkoutDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentExerciseIndex, setCurrentExerciseIndex] = useState(0);
  const [currentSetIndex, setCurrentSetIndex] = useState(0);

  // Rest timer state
  const [restTime, setRestTime] = useState(0);
  const [isResting, setIsResting] = useState(false);
  const [showRestTimer, setShowRestTimer] = useState(false);
  const restIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Set editing
  const [editingSet, setEditingSet] = useState<{
    exerciseId: number;
    setId: number;
    actualReps: string;
    actualWeight: string;
  } | null>(null);

  // Refresh when tab gains focus
  useFocusEffect(
    useCallback(() => {
      fetchActiveWorkout();
    }, []),
  );

  useEffect(() => {
    return () => {
      if (restIntervalRef.current) {
        clearInterval(restIntervalRef.current);
      }
    };
  }, []);

  const fetchActiveWorkout = async () => {
    try {
      setLoading(true);

      const response = await apiClient.get("/api/workouts/active");

      // Handle 204 / empty response
      if (!response.data || Object.keys(response.data).length === 0) {
        setWorkout(null);
        return;
      }

      setWorkout(response.data);

      // Find first incomplete set
      let foundIncomplete = false;
      response.data.exercises.forEach((ex: WorkoutExercise, exIdx: number) => {
        ex.sets.forEach((set: WorkoutSet, setIdx: number) => {
          if (!set.completed && !foundIncomplete) {
            setCurrentExerciseIndex(exIdx);
            setCurrentSetIndex(setIdx);
            foundIncomplete = true;
          }
        });
      });
    } catch (err: any) {
      console.error("Failed to fetch workout:", err);
      setWorkout(null);
    } finally {
      setLoading(false);
    }
  };

  const resumeWorkout = async () => {
    if (!workout) return;

    try {
      setLoading(true);
      await apiClient.post(`/api/workouts/${workout.id}/resume`);
      await fetchActiveWorkout();
      Alert.alert("Success", "Workout resumed!");
    } catch (err) {
      console.error("Failed to resume workout:", err);
      Alert.alert("Error", "Failed to resume workout");
    } finally {
      setLoading(false);
    }
  };

  const startRestTimer = (seconds: number = 90) => {
    setRestTime(seconds);
    setIsResting(true);
    setShowRestTimer(true);

    restIntervalRef.current = setInterval(() => {
      setRestTime((prev) => {
        if (prev <= 1) {
          if (restIntervalRef.current) {
            clearInterval(restIntervalRef.current);
          }
          setIsResting(false);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const skipRest = () => {
    if (restIntervalRef.current) {
      clearInterval(restIntervalRef.current);
    }
    setIsResting(false);
    setRestTime(0);
    setShowRestTimer(false);
  };

  const completeSet = async (
    exerciseId: number,
    setId: number,
    actualReps?: number,
    actualWeight?: number,
  ) => {
    if (!workout) return;

    try {
      await apiClient.put(
        `/api/workouts/${workout.id}/exercises/${exerciseId}/sets/${setId}`,
        {
          actualReps: actualReps || undefined,
          actualWeight: actualWeight || undefined,
          completed: true,
        },
      );

      // Update local state
      const updatedWorkout = { ...workout };
      const exercise = updatedWorkout.exercises.find(
        (ex) => ex.id === exerciseId,
      );
      if (exercise) {
        const set = exercise.sets.find((s) => s.id === setId);
        if (set) {
          set.completed = true;
          set.actualReps = actualReps;
          set.actualWeight = actualWeight;
        }
      }
      setWorkout(updatedWorkout);

      // Move to next set or exercise
      const currentExercise = workout.exercises[currentExerciseIndex];
      if (currentSetIndex < currentExercise.sets.length - 1) {
        setCurrentSetIndex(currentSetIndex + 1);
        startRestTimer();
      } else if (currentExerciseIndex < workout.exercises.length - 1) {
        setCurrentExerciseIndex(currentExerciseIndex + 1);
        setCurrentSetIndex(0);
        startRestTimer(120);
      } else {
        // Workout complete
        Alert.alert(
          "Great Job!",
          "You've completed all sets. Ready to finish?",
          [
            { text: "Keep Going", style: "cancel" },
            { text: "Finish Workout", onPress: finishWorkout },
          ],
        );
      }
    } catch (err) {
      console.error("Failed to complete set:", err);
      Alert.alert("Error", "Failed to update set");
    }
  };

  const openSetEditor = (
    exerciseId: number,
    setId: number,
    plannedReps?: number,
    plannedWeight?: number,
  ) => {
    setEditingSet({
      exerciseId,
      setId,
      actualReps: plannedReps?.toString() || "",
      actualWeight: plannedWeight?.toString() || "",
    });
  };

  const saveSetEdit = async () => {
    if (!editingSet) return;

    const actualReps = editingSet.actualReps
      ? parseInt(editingSet.actualReps)
      : undefined;
    const actualWeight = editingSet.actualWeight
      ? parseFloat(editingSet.actualWeight)
      : undefined;

    await completeSet(
      editingSet.exerciseId,
      editingSet.setId,
      actualReps,
      actualWeight,
    );
    setEditingSet(null);
  };

  const pauseWorkout = async () => {
    if (!workout) return;

    Alert.alert(
      "Pause Workout",
      "Your progress will be saved. You can resume this workout later.",
      [
        { text: "Cancel", style: "cancel" },
        {
          text: "Pause",
          onPress: async () => {
            try {
              setLoading(true);
              await apiClient.post(`/api/workouts/${workout.id}/pause`);
              await fetchActiveWorkout();
              Alert.alert("Workout Paused", "You can resume anytime!");
            } catch (err) {
              console.error("Failed to pause workout:", err);
              Alert.alert("Error", "Failed to pause workout");
            } finally {
              setLoading(false);
            }
          },
        },
      ],
    );
  };

  const finishWorkout = async () => {
    if (!workout) return;

    try {
      setLoading(true);
      await apiClient.post(`/api/workouts/${workout.id}/finish`);
      Alert.alert("Success", "Workout completed!", [
        { text: "OK", onPress: () => fetchActiveWorkout() },
      ]);
    } catch (err) {
      console.error("Failed to finish workout:", err);
      Alert.alert("Error", "Failed to finish workout");
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  const renderRestTimer = () => (
    <Modal visible={showRestTimer} transparent animationType="fade">
      <View style={styles.restTimerOverlay}>
        <View style={styles.restTimerModal}>
          <Text style={styles.restTimerTitle}>Rest Time</Text>
          <Text style={styles.restTimerTime}>{formatTime(restTime)}</Text>
          <View style={styles.restTimerButtons}>
            <TouchableOpacity
              style={styles.restTimerButton}
              onPress={() => setRestTime((prev) => prev + 15)}
            >
              <Text style={styles.restTimerButtonText}>+15s</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.restTimerButton, styles.skipButton]}
              onPress={skipRest}
            >
              <Text style={styles.restTimerButtonText}>Skip Rest</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );

  const renderSetEditor = () => {
    if (!editingSet) return null;

    return (
      <Modal visible transparent animationType="slide">
        <KeyboardAvoidingView
          behavior={Platform.OS === "ios" ? "padding" : "height"}
          style={styles.setEditorOverlay}
        >
          <TouchableOpacity
            style={styles.setEditorBackdrop}
            activeOpacity={1}
            onPress={() => setEditingSet(null)}
          />
          <View style={styles.setEditorModal}>
            <Text style={styles.setEditorTitle}>Log Set</Text>

            <View style={styles.setEditorInputs}>
              <View style={styles.setEditorInputGroup}>
                <Text style={styles.setEditorLabel}>Reps</Text>
                <TextInput
                  style={styles.setEditorInput}
                  value={editingSet.actualReps}
                  onChangeText={(v) =>
                    setEditingSet({ ...editingSet, actualReps: v })
                  }
                  keyboardType="number-pad"
                  placeholder="0"
                  placeholderTextColor="#999"
                />
              </View>

              <View style={styles.setEditorInputGroup}>
                <Text style={styles.setEditorLabel}>Weight (kg)</Text>
                <TextInput
                  style={styles.setEditorInput}
                  value={editingSet.actualWeight}
                  onChangeText={(v) =>
                    setEditingSet({ ...editingSet, actualWeight: v })
                  }
                  keyboardType="decimal-pad"
                  placeholder="0"
                  placeholderTextColor="#999"
                />
              </View>
            </View>

            <View style={styles.setEditorButtons}>
              <TouchableOpacity
                style={[styles.setEditorButton, styles.cancelButton]}
                onPress={() => setEditingSet(null)}
              >
                <Text style={styles.cancelButtonText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.setEditorButton, styles.saveButton]}
                onPress={saveSetEdit}
              >
                <Text style={styles.saveButtonText}>Complete Set</Text>
              </TouchableOpacity>
            </View>
          </View>
        </KeyboardAvoidingView>
      </Modal>
    );
  };

  const renderExerciseSet = (
    exercise: WorkoutExercise,
    set: WorkoutSet,
    setIdx: number,
  ) => {
    const isCurrentSet =
      exercise.orderIndex === currentExerciseIndex &&
      setIdx === currentSetIndex;

    return (
      <View
        key={set.id}
        style={[
          styles.setRow,
          set.completed && styles.completedSet,
          isCurrentSet && styles.currentSet,
        ]}
      >
        <View style={styles.setInfo}>
          <Text style={styles.setNumber}>Set {set.setNumber}</Text>
          <Text style={styles.setDetails}>
            {set.plannedReps ? `${set.plannedReps} reps` : ""}
            {set.plannedWeight ? ` × ${set.plannedWeight}kg` : ""}
          </Text>
        </View>

        {set.completed ? (
          <View style={styles.completedInfo}>
            <Text style={styles.completedText}>✓ Done</Text>
            {(set.actualReps || set.actualWeight) && (
              <Text style={styles.actualText}>
                {set.actualReps ? `${set.actualReps} reps` : ""}
                {set.actualWeight ? ` × ${set.actualWeight}kg` : ""}
              </Text>
            )}
          </View>
        ) : isCurrentSet ? (
          <TouchableOpacity
            style={styles.completeButton}
            onPress={() =>
              openSetEditor(
                exercise.id,
                set.id,
                set.plannedReps,
                set.plannedWeight,
              )
            }
          >
            <Text style={styles.completeButtonText}>Complete</Text>
          </TouchableOpacity>
        ) : (
          <View style={styles.pendingIndicator}>
            <Text style={styles.pendingText}>Pending</Text>
          </View>
        )}
      </View>
    );
  };

  const renderExercise = (exercise: WorkoutExercise, index: number) => {
    const isCurrentExercise = index === currentExerciseIndex;
    const completedSets = exercise.sets.filter((s) => s.completed).length;
    const totalSets = exercise.sets.length;
    const isCompleted = completedSets === totalSets;

    return (
      <View
        key={exercise.id}
        style={[
          styles.exerciseCard,
          isCurrentExercise && styles.currentExerciseCard,
        ]}
      >
        <View style={styles.exerciseHeader}>
          {exercise.imageUrl && (
            <Image
              source={{ uri: exercise.imageUrl }}
              style={styles.exerciseImage}
              resizeMode="cover"
            />
          )}
          <View style={styles.exerciseInfo}>
            <Text style={styles.exerciseName}>
              {index + 1}. {exercise.exerciseName}
            </Text>
            <Text style={styles.exerciseProgress}>
              {completedSets}/{totalSets} sets {isCompleted ? "✓" : ""}
            </Text>
          </View>
        </View>

        <View style={styles.setsContainer}>
          {exercise.sets.map((set, setIdx) =>
            renderExerciseSet(exercise, set, setIdx),
          )}
        </View>
      </View>
    );
  };

  const renderNoWorkout = () => (
    <View style={styles.centerContainer}>
      <Text style={styles.noWorkoutEmoji}>💪</Text>
      <Text style={styles.noWorkoutTitle}>No Active Workout</Text>
      <Text style={styles.noWorkoutSubtitle}>
        Go to Workout Templates to start a workout
      </Text>
      <TouchableOpacity
        style={styles.goToTemplatesButton}
        onPress={() => router.push("/home/(tabs)/workout_template")}
      >
        <Text style={styles.goToTemplatesText}>View Templates</Text>
      </TouchableOpacity>
    </View>
  );

  const renderPausedWorkout = () => (
    <View style={styles.centerContainer}>
      <Text style={styles.pausedEmoji}>⏸️</Text>
      <Text style={styles.pausedTitle}>Workout Paused</Text>
      <Text style={styles.pausedSubtitle}>{workout?.templateName}</Text>
      <Text style={styles.pausedProgress}>
        {
          workout?.exercises.filter((ex) => ex.sets.every((s) => s.completed))
            .length
        }
        /{workout?.exercises.length} exercises completed
      </Text>
      <TouchableOpacity style={styles.resumeButton} onPress={resumeWorkout}>
        <Text style={styles.resumeButtonText}>Resume Workout</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.cancelWorkoutButton}
        onPress={finishWorkout}
      >
        <Text style={styles.cancelWorkoutText}>Finish Workout</Text>
      </TouchableOpacity>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#3AAFA9" />
        <Text style={styles.loadingText}>Loading workout...</Text>
      </View>
    );
  }

  if (!workout) {
    return renderNoWorkout();
  }

  if (workout.status === "PAUSED") {
    return renderPausedWorkout();
  }

  const completedExercises = workout.exercises.filter((ex) =>
    ex.sets.every((s) => s.completed),
  ).length;

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <View style={styles.headerTop}>
          <View style={styles.headerTitleContainer}>
            <Text style={styles.headerTitle}>{workout.templateName}</Text>
          </View>
          <View style={styles.headerButtons}>
            <TouchableOpacity onPress={pauseWorkout} style={styles.pauseButton}>
              <Text style={styles.pauseButtonText}>Pause</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={finishWorkout}>
              <Text style={styles.finishButton}>Finish</Text>
            </TouchableOpacity>
          </View>
        </View>

        <View style={styles.progressBar}>
          <View
            style={[
              styles.progressFill,
              {
                width: `${(completedExercises / workout.exercises.length) * 100}%`,
              },
            ]}
          />
        </View>
        <Text style={styles.progressText}>
          {completedExercises}/{workout.exercises.length} exercises completed
        </Text>
      </View>

      <ScrollView style={styles.content}>
        {workout.exercises.map((ex, idx) => renderExercise(ex, idx))}
      </ScrollView>

      {renderRestTimer()}
      {renderSetEditor()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f5f5f5" },
  centerContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 32,
    backgroundColor: "#f5f5f5",
  },
  loadingText: { marginTop: 12, color: "#666" },
  noWorkoutEmoji: { fontSize: 72, marginBottom: 16 },
  noWorkoutTitle: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#333",
    marginBottom: 8,
  },
  noWorkoutSubtitle: {
    fontSize: 16,
    color: "#666",
    textAlign: "center",
    marginBottom: 24,
  },
  goToTemplatesButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 32,
    paddingVertical: 16,
    borderRadius: 8,
  },
  goToTemplatesText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },
  pausedEmoji: { fontSize: 72, marginBottom: 16 },
  pausedTitle: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#333",
    marginBottom: 8,
  },
  pausedSubtitle: {
    fontSize: 18,
    color: "#666",
    textAlign: "center",
    marginBottom: 8,
  },
  pausedProgress: {
    fontSize: 16,
    color: "#999",
    textAlign: "center",
    marginBottom: 24,
  },
  resumeButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 48,
    paddingVertical: 16,
    borderRadius: 8,
    marginBottom: 12,
  },
  resumeButtonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },
  cancelWorkoutButton: {
    backgroundColor: "#f0f0f0",
    paddingHorizontal: 32,
    paddingVertical: 12,
    borderRadius: 8,
  },
  cancelWorkoutText: {
    color: "#666",
    fontSize: 14,
    fontWeight: "600",
  },
  header: {
    backgroundColor: "#fff",
    paddingTop: 50,
    paddingHorizontal: 16,
    paddingBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
  headerTop: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  headerTitleContainer: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  headerTitle: { fontSize: 20, fontWeight: "bold" },
  headerButtons: {
    flexDirection: "row",
    gap: 8,
    alignItems: "center",
  },
  pauseButton: {
    backgroundColor: "#f0f0f0",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
  },
  pauseButtonText: {
    fontSize: 14,
    color: "#666",
    fontWeight: "600",
  },
  finishButton: { fontSize: 16, color: "#3AAFA9", fontWeight: "600" },
  progressBar: {
    height: 8,
    backgroundColor: "#e0e0e0",
    borderRadius: 4,
    overflow: "hidden",
    marginBottom: 8,
  },
  progressFill: { height: "100%", backgroundColor: "#3AAFA9" },
  progressText: { fontSize: 14, color: "#666", textAlign: "center" },
  content: { flex: 1, padding: 16 },
  exerciseCard: {
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  currentExerciseCard: {
    borderWidth: 2,
    borderColor: "#3AAFA9",
  },
  exerciseHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 16,
  },
  exerciseImage: {
    width: 60,
    height: 60,
    borderRadius: 8,
    marginRight: 12,
  },
  exerciseInfo: { flex: 1 },
  exerciseName: { fontSize: 18, fontWeight: "600", marginBottom: 4 },
  exerciseProgress: { fontSize: 14, color: "#666" },
  setsContainer: { gap: 8 },
  setRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    backgroundColor: "#f8f8f8",
    padding: 12,
    borderRadius: 8,
  },
  completedSet: { backgroundColor: "#e8f5f4" },
  currentSet: {
    backgroundColor: "#fff",
    borderWidth: 2,
    borderColor: "#3AAFA9",
  },
  setInfo: { flex: 1 },
  setNumber: { fontSize: 14, fontWeight: "600", marginBottom: 2 },
  setDetails: { fontSize: 13, color: "#666" },
  completeButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 6,
  },
  completeButtonText: { color: "#fff", fontWeight: "600", fontSize: 14 },
  completedInfo: { alignItems: "flex-end" },
  completedText: { fontSize: 14, color: "#2B7A78", fontWeight: "600" },
  actualText: { fontSize: 12, color: "#666", marginTop: 2 },
  pendingIndicator: { paddingHorizontal: 16, paddingVertical: 8 },
  pendingText: { fontSize: 14, color: "#999" },
  restTimerOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.7)",
    justifyContent: "center",
    alignItems: "center",
  },
  restTimerModal: {
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 32,
    alignItems: "center",
    minWidth: 280,
  },
  restTimerTitle: { fontSize: 18, fontWeight: "600", marginBottom: 16 },
  restTimerTime: {
    fontSize: 64,
    fontWeight: "bold",
    color: "#3AAFA9",
    marginBottom: 24,
  },
  restTimerButtons: { flexDirection: "row", gap: 12 },
  restTimerButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderRadius: 8,
  },
  skipButton: { backgroundColor: "#666" },
  restTimerButtonText: { color: "#fff", fontWeight: "600", fontSize: 14 },
  setEditorOverlay: {
    flex: 1,
    justifyContent: "flex-end",
  },
  setEditorBackdrop: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.5)",
  },
  setEditorModal: {
    backgroundColor: "#fff",
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 24,
    paddingBottom: Platform.OS === "ios" ? 34 : 24,
  },
  setEditorTitle: { fontSize: 20, fontWeight: "bold", marginBottom: 20 },
  setEditorInputs: { flexDirection: "row", gap: 16, marginBottom: 24 },
  setEditorInputGroup: { flex: 1 },
  setEditorLabel: {
    fontSize: 14,
    fontWeight: "600",
    marginBottom: 8,
    color: "#333",
  },
  setEditorInput: {
    backgroundColor: "#f0f0f0",
    padding: 16,
    borderRadius: 8,
    fontSize: 18,
    textAlign: "center",
    color: "#000",
  },
  setEditorButtons: { flexDirection: "row", gap: 12 },
  setEditorButton: {
    flex: 1,
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
  },
  cancelButton: { backgroundColor: "#f0f0f0" },
  cancelButtonText: { color: "#666", fontWeight: "600", fontSize: 16 },
  saveButton: { backgroundColor: "#3AAFA9" },
  saveButtonText: { color: "#fff", fontWeight: "600", fontSize: 16 },
});
