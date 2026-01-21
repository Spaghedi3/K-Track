import apiClient from "@/app/services/apiClient";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Image,
  Modal,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import ModernDropdownMultiselect from "./ModernDropdownMultiselect";

interface Exercise {
  id: number;
  exerciseId: string;
  name: string;
  imageUrl?: string;
  targetMuscles: string[];
}

interface TemplateSet {
  reps?: number;
  weight?: number;
  durationSeconds?: number;
  distance?: number;
}

interface TemplateExercise {
  exerciseId: number;
  exerciseName: string;
  exerciseImageUrl?: string;
  orderIndex: number;
  sets: TemplateSet[];
}

interface Props {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

type ModalView = "main" | "exercise-picker";

export default function AddWorkoutTemplateModal({
  visible,
  onClose,
  onSuccess,
}: Props) {
  const [loading, setLoading] = useState(false);
  const [loadingExercises, setLoadingExercises] = useState(false);
  const [currentView, setCurrentView] = useState<ModalView>("main");

  const [templateName, setTemplateName] = useState("");
  const [templateDescription, setTemplateDescription] = useState("");
  const [selectedExercises, setSelectedExercises] = useState<
    TemplateExercise[]
  >([]);

  const [availableExercises, setAvailableExercises] = useState<Exercise[]>([]);
  const [selectedExerciseIds, setSelectedExerciseIds] = useState<string[]>([]);

  useEffect(() => {
    if (visible) {
      fetchExercises();
      setCurrentView("main");
    }
  }, [visible]);

  const fetchExercises = async () => {
    try {
      setLoadingExercises(true);
      const preloaded = await apiClient.get("/api/exercises/preloaded");
      const custom = await apiClient.get("/api/exercises/custom");
      setAvailableExercises([...preloaded.data, ...custom.data]);
    } catch (err) {
      console.error("Failed to load exercises:", err);
      Alert.alert("Error", "Failed to load exercises");
    } finally {
      setLoadingExercises(false);
    }
  };

  const handleExerciseSelectionChange = (ids: string[]) => {
    setSelectedExerciseIds(ids);
  };

  const addSelectedExercises = () => {
    const newExercises: TemplateExercise[] = selectedExerciseIds
      .map((id, index) => {
        const exercise = availableExercises.find((e) => e.id === parseInt(id));
        if (!exercise) return null;

        return {
          exerciseId: exercise.id,
          exerciseName: exercise.name,
          exerciseImageUrl: exercise.imageUrl,
          orderIndex: selectedExercises.length + index,
          sets: [
            {
              reps: 10,
              weight: 0,
              durationSeconds: undefined,
              distance: undefined,
            },
          ],
        } as TemplateExercise;
      })
      .filter((e): e is TemplateExercise => e !== null)
      .filter(
        (newEx) =>
          !selectedExercises.some((ex) => ex.exerciseId === newEx.exerciseId),
      );

    setSelectedExercises([...selectedExercises, ...newExercises]);
    setCurrentView("main");
    setSelectedExerciseIds([]);
  };

  const removeExercise = (index: number) => {
    const updated = selectedExercises.filter((_, i) => i !== index);
    updated.forEach((ex, idx) => {
      ex.orderIndex = idx;
    });
    setSelectedExercises(updated);
  };

  const addSetToExercise = (exerciseIndex: number) => {
    const updated = [...selectedExercises];
    updated[exerciseIndex].sets.push({
      reps: 10,
      weight: 0,
      durationSeconds: undefined,
      distance: undefined,
    });
    setSelectedExercises(updated);
  };

  const removeSetFromExercise = (exerciseIndex: number, setIndex: number) => {
    const updated = [...selectedExercises];
    if (updated[exerciseIndex].sets.length > 1) {
      updated[exerciseIndex].sets = updated[exerciseIndex].sets.filter(
        (_, i) => i !== setIndex,
      );
      setSelectedExercises(updated);
    }
  };

  const updateSet = (
    exerciseIndex: number,
    setIndex: number,
    field: keyof TemplateSet,
    value: string,
  ) => {
    const updated = [...selectedExercises];
    const numValue = value === "" ? undefined : parseFloat(value);
    updated[exerciseIndex].sets[setIndex][field] = numValue as any;
    setSelectedExercises(updated);
  };

  const handleSubmit = async () => {
    if (!templateName.trim()) {
      Alert.alert("Validation", "Please enter a template name");
      return;
    }

    if (selectedExercises.length === 0) {
      Alert.alert("Validation", "Please add at least one exercise");
      return;
    }

    try {
      setLoading(true);

      const payload = {
        name: templateName,
        description: templateDescription || null,
        exercises: selectedExercises.map((ex) => ({
          exerciseId: ex.exerciseId,
          orderIndex: ex.orderIndex,
          sets: ex.sets.map((set) => ({
            reps: set.reps || null,
            weight: set.weight || null,
            durationSeconds: set.durationSeconds || null,
            distance: set.distance || null,
          })),
        })),
      };

      await apiClient.post("/api/workout-templates", payload);

      // Reset form
      setTemplateName("");
      setTemplateDescription("");
      setSelectedExercises([]);
      setSelectedExerciseIds([]);
      setCurrentView("main");
      onClose();
      onSuccess();
    } catch (err) {
      console.error("Failed to create template:", err);
      Alert.alert("Error", "Failed to create workout template");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setCurrentView("main");
    onClose();
  };

  const exerciseOptions = availableExercises.map((ex) => ({
    label: ex.name,
    value: ex.id.toString(),
  }));

  const renderExercisePicker = () => (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Select Exercises</Text>
        <TouchableOpacity
          onPress={() => setCurrentView("main")}
          style={styles.closeButton}
        >
          <Text style={styles.close}>←</Text>
        </TouchableOpacity>
      </View>

      {loadingExercises ? (
        <View style={styles.centerContainer}>
          <ActivityIndicator size="large" color="#3AAFA9" />
        </View>
      ) : (
        <ScrollView style={styles.pickerContent}>
          <View style={styles.dropdownContainer}>
            <ModernDropdownMultiselect
              options={exerciseOptions}
              value={selectedExerciseIds}
              onValuesChange={handleExerciseSelectionChange}
              placeholder="Search and select exercises"
              multiSelect
            />
          </View>

          <TouchableOpacity
            style={[
              styles.addButton,
              selectedExerciseIds.length === 0 && styles.disabled,
            ]}
            onPress={addSelectedExercises}
            disabled={selectedExerciseIds.length === 0}
          >
            <Text style={styles.addButtonText}>
              Add {selectedExerciseIds.length} Exercise
              {selectedExerciseIds.length !== 1 ? "s" : ""}
            </Text>
          </TouchableOpacity>

          {selectedExerciseIds.length > 0 && (
            <View style={styles.previewSection}>
              <Text style={styles.previewTitle}>Selected:</Text>
              {selectedExerciseIds.map((id) => {
                const exercise = availableExercises.find(
                  (e) => e.id === parseInt(id),
                );
                return exercise ? (
                  <View key={id} style={styles.previewItem}>
                    {exercise.imageUrl && (
                      <Image
                        source={{ uri: exercise.imageUrl }}
                        style={styles.previewImage}
                        resizeMode="cover"
                      />
                    )}
                    <Text style={styles.previewName}>{exercise.name}</Text>
                  </View>
                ) : null;
              })}
            </View>
          )}
        </ScrollView>
      )}
    </View>
  );

  const renderSelectedExercise = (
    exercise: TemplateExercise,
    index: number,
  ) => (
    <View key={index} style={styles.exerciseCard}>
      <View style={styles.exerciseCardHeader}>
        {exercise.exerciseImageUrl && (
          <Image
            source={{ uri: exercise.exerciseImageUrl }}
            style={styles.exerciseCardImage}
            resizeMode="cover"
          />
        )}
        <Text style={styles.exerciseCardName}>
          {index + 1}. {exercise.exerciseName}
        </Text>
        <TouchableOpacity onPress={() => removeExercise(index)}>
          <Text style={styles.removeText}>✕</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.setsSection}>
        <Text style={styles.setsLabel}>Sets ({exercise.sets.length})</Text>
        {exercise.sets.map((set, setIdx) => (
          <View key={setIdx} style={styles.setCard}>
            <View style={styles.setHeader}>
              <Text style={styles.setNumber}>Set {setIdx + 1}</Text>
              {exercise.sets.length > 1 && (
                <TouchableOpacity
                  onPress={() => removeSetFromExercise(index, setIdx)}
                >
                  <Text style={styles.removeSetText}>✕</Text>
                </TouchableOpacity>
              )}
            </View>

            <View style={styles.setInputRow}>
              <View style={styles.setInputGroup}>
                <Text style={styles.inputLabel}>Reps</Text>
                <TextInput
                  style={styles.setInput}
                  value={set.reps?.toString() || ""}
                  onChangeText={(v) => updateSet(index, setIdx, "reps", v)}
                  keyboardType="number-pad"
                  placeholder="0"
                />
              </View>

              <View style={styles.setInputGroup}>
                <Text style={styles.inputLabel}>Weight (kg)</Text>
                <TextInput
                  style={styles.setInput}
                  value={set.weight?.toString() || ""}
                  onChangeText={(v) => updateSet(index, setIdx, "weight", v)}
                  keyboardType="decimal-pad"
                  placeholder="0"
                />
              </View>

              <View style={styles.setInputGroup}>
                <Text style={styles.inputLabel}>Duration (s)</Text>
                <TextInput
                  style={styles.setInput}
                  value={set.durationSeconds?.toString() || ""}
                  onChangeText={(v) =>
                    updateSet(index, setIdx, "durationSeconds", v)
                  }
                  keyboardType="number-pad"
                  placeholder="0"
                />
              </View>

              <View style={styles.setInputGroup}>
                <Text style={styles.inputLabel}>Distance (m)</Text>
                <TextInput
                  style={styles.setInput}
                  value={set.distance?.toString() || ""}
                  onChangeText={(v) => updateSet(index, setIdx, "distance", v)}
                  keyboardType="decimal-pad"
                  placeholder="0"
                />
              </View>
            </View>
          </View>
        ))}

        <TouchableOpacity
          style={styles.addSetButton}
          onPress={() => addSetToExercise(index)}
        >
          <Text style={styles.addSetText}>+ Add Set</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderMainView = () => (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>New Workout Template</Text>
        <TouchableOpacity onPress={handleClose} style={styles.closeButton}>
          <Text style={styles.close}>✕</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.group}>
          <Text style={styles.label}>Template Name *</Text>
          <TextInput
            style={styles.input}
            value={templateName}
            onChangeText={setTemplateName}
            placeholder="e.g., Push Day, Full Body"
          />
        </View>

        <View style={styles.group}>
          <Text style={styles.label}>Description</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={templateDescription}
            onChangeText={setTemplateDescription}
            placeholder="Optional description"
            multiline
            numberOfLines={3}
          />
        </View>

        <View style={styles.group}>
          <View style={styles.exercisesHeader}>
            <Text style={styles.label}>Exercises *</Text>
            <TouchableOpacity
              style={styles.addExerciseButton}
              onPress={() => setCurrentView("exercise-picker")}
            >
              <Text style={styles.addExerciseText}>+ Add Exercises</Text>
            </TouchableOpacity>
          </View>

          {selectedExercises.length === 0 ? (
            <View style={styles.emptyExercises}>
              <Text style={styles.emptyText}>No exercises added yet</Text>
            </View>
          ) : (
            selectedExercises.map((ex, idx) => renderSelectedExercise(ex, idx))
          )}
        </View>

        <TouchableOpacity
          style={[
            styles.submitButton,
            (!templateName || selectedExercises.length === 0 || loading) &&
              styles.disabled,
          ]}
          onPress={handleSubmit}
          disabled={!templateName || selectedExercises.length === 0 || loading}
        >
          <Text style={styles.submitText}>
            {loading ? "Creating..." : "Create Template"}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </View>
  );

  return (
    <Modal visible={visible} animationType="slide">
      {currentView === "main" ? renderMainView() : renderExercisePicker()}
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff" },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    paddingTop: 50,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
  title: { fontSize: 20, fontWeight: "bold" },
  closeButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: "#f0f0f0",
    justifyContent: "center",
    alignItems: "center",
  },
  close: { fontSize: 18, color: "#666" },
  content: { padding: 16, paddingBottom: 40 },
  group: { marginBottom: 24 },
  label: { fontSize: 16, fontWeight: "600", marginBottom: 8, color: "#333" },
  input: {
    backgroundColor: "#f0f0f0",
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
  },
  textArea: { minHeight: 80, textAlignVertical: "top" },
  exercisesHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  addExerciseButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 6,
  },
  addExerciseText: { color: "#fff", fontWeight: "600", fontSize: 14 },
  emptyExercises: {
    padding: 32,
    alignItems: "center",
    backgroundColor: "#f8f8f8",
    borderRadius: 8,
    borderWidth: 2,
    borderColor: "#e0e0e0",
    borderStyle: "dashed",
  },
  emptyText: { color: "#999", fontSize: 14 },
  exerciseCard: {
    backgroundColor: "#f8f8f8",
    padding: 12,
    borderRadius: 8,
    marginBottom: 12,
  },
  exerciseCardHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  exerciseCardImage: {
    width: 50,
    height: 50,
    borderRadius: 6,
    marginRight: 10,
  },
  exerciseCardName: { fontSize: 16, fontWeight: "600", flex: 1 },
  removeText: { fontSize: 18, color: "#ff3b30", paddingLeft: 8 },
  setsSection: { marginTop: 8 },
  setsLabel: {
    fontSize: 14,
    fontWeight: "600",
    color: "#666",
    marginBottom: 8,
  },
  setCard: {
    backgroundColor: "#fff",
    padding: 10,
    borderRadius: 6,
    marginBottom: 8,
  },
  setHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 8,
  },
  setNumber: { fontSize: 13, fontWeight: "600", color: "#333" },
  removeSetText: { fontSize: 16, color: "#ff3b30" },
  setInputRow: { flexDirection: "row", gap: 8 },
  setInputGroup: { flex: 1 },
  inputLabel: { fontSize: 10, color: "#666", marginBottom: 4 },
  setInput: {
    backgroundColor: "#f0f0f0",
    padding: 6,
    borderRadius: 4,
    fontSize: 14,
    textAlign: "center",
  },
  addSetButton: {
    backgroundColor: "#e8f5f4",
    padding: 10,
    borderRadius: 6,
    alignItems: "center",
  },
  addSetText: { color: "#2B7A78", fontWeight: "600", fontSize: 13 },
  submitButton: {
    backgroundColor: "#3AAFA9",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 8,
  },
  disabled: { backgroundColor: "#9ADAD6" },
  submitText: { color: "#fff", fontSize: 18, fontWeight: "600" },
  centerContainer: { flex: 1, justifyContent: "center", alignItems: "center" },
  pickerContent: { flex: 1, padding: 16 },
  dropdownContainer: { marginBottom: 16 },
  addButton: {
    backgroundColor: "#3AAFA9",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
    marginBottom: 16,
  },
  addButtonText: { color: "#fff", fontSize: 16, fontWeight: "600" },
  previewSection: {
    backgroundColor: "#f8f8f8",
    padding: 12,
    borderRadius: 8,
  },
  previewTitle: { fontSize: 14, fontWeight: "600", marginBottom: 8 },
  previewItem: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#fff",
    padding: 8,
    borderRadius: 6,
    marginBottom: 6,
  },
  previewImage: {
    width: 40,
    height: 40,
    borderRadius: 4,
    marginRight: 10,
  },
  previewName: { fontSize: 14, flex: 1 },
});
