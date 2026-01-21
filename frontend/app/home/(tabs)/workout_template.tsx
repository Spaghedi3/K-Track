import AddWorkoutTemplateModal from "@/app/componets/AddWorkoutTemplateModal";
import EditWorkoutTemplateModal from "@/app/componets/EditWorkoutTemplateModal";
import apiClient from "@/app/services/apiClient";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Image,
  Modal,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

interface TemplateSet {
  reps?: number;
  weight?: number;
  durationSeconds?: number;
  distance?: number;
}

interface TemplateExercise {
  exerciseId: number;
  exerciseName: string;
  imageUrl: string;
  orderIndex: number;
  sets: TemplateSet[];
}

interface WorkoutTemplate {
  id: number;
  name: string;
  description?: string;
  exercises: TemplateExercise[];
}

export default function WorkoutTemplatesTab() {
  const router = useRouter();
  const [templates, setTemplates] = useState<WorkoutTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedTemplate, setSelectedTemplate] =
    useState<WorkoutTemplate | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingTemplate, setEditingTemplate] =
    useState<WorkoutTemplate | null>(null);

  useEffect(() => {
    fetchTemplates();
  }, []);

  const fetchTemplates = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get("/api/workout-templates");
      setTemplates(response.data);
      setError(null);
    } catch (err) {
      console.error(err);
      setError("Failed to load workout templates");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await apiClient.delete(`/api/workout-templates/${id}`);
      fetchTemplates();
      setSelectedTemplate(null);
    } catch (err) {
      console.error("Failed to delete template:", err);
      setError("Failed to delete template");
    }
  };

  const handleEdit = (template: WorkoutTemplate) => {
    setEditingTemplate(template);
    setShowEditModal(true);
    setSelectedTemplate(null);
  };

  const handleStartWorkout = async (templateId: number) => {
    try {
      setSelectedTemplate(null);
      setLoading(true);

      // 1) Check active workout
      const activeWorkoutResponse = await apiClient.get("/api/workouts/active");

      if (
        activeWorkoutResponse.data &&
        Object.keys(activeWorkoutResponse.data).length > 0
      ) {
        const activeWorkoutId = activeWorkoutResponse.data.id;

        // Ask user what they want to do
        Alert.alert(
          "Active Workout Found",
          "You already have an active workout. What would you like to do?",
          [
            { text: "Cancel", style: "cancel" },

            {
              text: "Continue Active Workout",
              onPress: () => router.push("/home/(tabs)/start_workout"),
            },

            {
              text: "Start New Workout",
              onPress: async () => {
                try {
                  // Finish old workout first
                  await apiClient.post(
                    `/api/workouts/${activeWorkoutId}/finish`,
                  );

                  // Then start a new one
                  await apiClient.post("/api/workouts/start", { templateId });

                  // Navigate
                  router.push("/home/(tabs)/start_workout");
                } catch (err) {
                  console.error("Failed to start new workout:", err);
                  Alert.alert("Error", "Failed to start workout");
                }
              },
            },
          ],
        );

        return;
      }

      // No active workout -> start new one
      await apiClient.post("/api/workouts/start", { templateId });
      router.push("/home/(tabs)/start_workout");
    } catch (err: any) {
      console.error("Failed to start workout:", err);
      Alert.alert("Error", "Failed to start workout");
    } finally {
      setLoading(false);
    }
  };

  const filteredTemplates = templates.filter((template) =>
    template.name.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const formatSetInfo = (set: TemplateSet): string => {
    const parts: string[] = [];
    if (set.reps) parts.push(`${set.reps} reps`);
    if (set.weight) parts.push(`${set.weight}kg`);
    if (set.durationSeconds) parts.push(`${set.durationSeconds}s`);
    if (set.distance) parts.push(`${set.distance}m`);
    return parts.join(" • ") || "No data";
  };

  const renderTemplateItem = ({ item }: { item: WorkoutTemplate }) => (
    <TouchableOpacity
      style={styles.templateCard}
      onPress={() => setSelectedTemplate(item)}
    >
      <View style={styles.templateHeader}>
        <Text style={styles.templateName}>{item.name}</Text>
        <Text style={styles.exerciseCount}>
          {item.exercises.length} exercise
          {item.exercises.length !== 1 ? "s" : ""}
        </Text>
      </View>

      {item.description && (
        <Text style={styles.templateDescription} numberOfLines={2}>
          {item.description}
        </Text>
      )}

      <View style={styles.exercisePreview}>
        {item.exercises.slice(0, 3).map((ex, idx) => (
          <View key={idx} style={styles.exercisePreviewRow}>
            {ex.imageUrl && (
              <Image
                source={{ uri: ex.imageUrl }}
                style={styles.exercisePreviewImage}
                resizeMode="cover"
              />
            )}
            <Text style={styles.exercisePreviewText} numberOfLines={1}>
              • {ex.exerciseName} ({ex.sets.length} set
              {ex.sets.length !== 1 ? "s" : ""})
            </Text>
          </View>
        ))}
        {item.exercises.length > 3 && (
          <Text style={styles.moreText}>+{item.exercises.length - 3} more</Text>
        )}
      </View>
    </TouchableOpacity>
  );

  const renderTemplateDetail = () => {
    if (!selectedTemplate) return null;

    return (
      <Modal visible animationType="slide">
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Template Details</Text>
            <TouchableOpacity onPress={() => setSelectedTemplate(null)}>
              <Text style={styles.modalCloseText}>✕</Text>
            </TouchableOpacity>
          </View>

          <ScrollView style={styles.modalContent}>
            <Text style={styles.detailName}>{selectedTemplate.name}</Text>

            {selectedTemplate.description && (
              <View style={styles.section}>
                <Text style={styles.sectionTitle}>Description</Text>
                <Text style={styles.text}>{selectedTemplate.description}</Text>
              </View>
            )}

            <View style={styles.section}>
              <Text style={styles.sectionTitle}>Exercises</Text>
              {selectedTemplate.exercises.map((ex, idx) => (
                <View key={idx} style={styles.exerciseDetailCard}>
                  <View style={styles.exerciseDetailHeader}>
                    {ex.imageUrl && (
                      <Image
                        source={{ uri: ex.imageUrl }}
                        style={styles.exerciseDetailImage}
                        resizeMode="cover"
                      />
                    )}
                    <Text style={styles.exerciseDetailName}>
                      {idx + 1}. {ex.exerciseName}
                    </Text>
                  </View>

                  <View style={styles.setsContainer}>
                    <Text style={styles.setsTitle}>
                      Sets ({ex.sets.length})
                    </Text>
                    {ex.sets.map((set, setIdx) => (
                      <View key={setIdx} style={styles.setRow}>
                        <Text style={styles.setNumber}>Set {setIdx + 1}</Text>
                        <Text style={styles.setInfo}>{formatSetInfo(set)}</Text>
                      </View>
                    ))}
                  </View>
                </View>
              ))}
            </View>

            <View style={styles.actionButtonsContainer}>
              <TouchableOpacity
                style={styles.startWorkoutButton}
                onPress={() => handleStartWorkout(selectedTemplate.id)}
              >
                <Text style={styles.startWorkoutButtonText}>Start Workout</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.editButton}
                onPress={() => handleEdit(selectedTemplate)}
              >
                <Text style={styles.editButtonText}>Edit Template</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.deleteButton}
                onPress={() => handleDelete(selectedTemplate.id)}
              >
                <Text style={styles.deleteButtonText}>Delete Template</Text>
              </TouchableOpacity>
            </View>
          </ScrollView>
        </View>
      </Modal>
    );
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#007AFF" />
        <Text style={styles.loadingText}>Loading templates...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>{error}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={fetchTemplates}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Workout Templates</Text>

        <TextInput
          style={styles.searchInput}
          placeholder="Search templates..."
          value={searchQuery}
          onChangeText={setSearchQuery}
          placeholderTextColor="#999"
        />
      </View>

      {filteredTemplates.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>No templates yet</Text>
          <Text style={styles.emptySubtext}>
            Create your first workout template
          </Text>
        </View>
      ) : (
        <FlatList
          data={filteredTemplates}
          renderItem={renderTemplateItem}
          keyExtractor={(item) => item.id.toString()}
          contentContainerStyle={styles.listContainer}
        />
      )}

      <TouchableOpacity
        style={styles.fab}
        onPress={() => setShowAddModal(true)}
      >
        <Text style={styles.fabText}>+</Text>
      </TouchableOpacity>

      {renderTemplateDetail()}

      <AddWorkoutTemplateModal
        visible={showAddModal}
        onClose={() => setShowAddModal(false)}
        onSuccess={fetchTemplates}
      />

      <EditWorkoutTemplateModal
        visible={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setEditingTemplate(null);
        }}
        onSuccess={fetchTemplates}
        template={editingTemplate}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f5f5f5" },
  centerContainer: { flex: 1, justifyContent: "center", alignItems: "center" },
  header: {
    backgroundColor: "#fff",
    padding: 16,
    paddingTop: 50,
    borderBottomWidth: 1,
    borderBottomColor: "#e0e0e0",
  },
  title: { fontSize: 28, fontWeight: "bold", marginBottom: 16 },
  searchInput: {
    backgroundColor: "#f0f0f0",
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    color: "#000",
  },
  listContainer: { padding: 16 },
  templateCard: {
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  templateHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 8,
  },
  templateName: { fontSize: 18, fontWeight: "600", flex: 1 },
  exerciseCount: {
    fontSize: 14,
    color: "#666",
    backgroundColor: "#f0f0f0",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
  },
  templateDescription: {
    fontSize: 14,
    color: "#666",
    marginBottom: 12,
  },
  exercisePreview: { marginTop: 8 },
  exercisePreviewRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 6,
  },
  exercisePreviewImage: {
    width: 40,
    height: 40,
    borderRadius: 6,
    marginRight: 8,
  },
  exercisePreviewText: { fontSize: 14, color: "#333", flex: 1 },
  moreText: { fontSize: 12, color: "#3AAFA9", fontWeight: "600", marginTop: 4 },
  emptyContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 32,
  },
  emptyText: {
    fontSize: 20,
    fontWeight: "600",
    color: "#666",
    marginBottom: 8,
  },
  emptySubtext: { fontSize: 16, color: "#999" },
  fab: {
    position: "absolute",
    right: 20,
    bottom: 20,
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: "#2B7A78",
    justifyContent: "center",
    alignItems: "center",
  },
  fabText: { color: "#fff", fontSize: 32 },
  modalContainer: { flex: 1, backgroundColor: "#fff" },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 16,
    paddingTop: 50,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
  modalTitle: { fontSize: 20, fontWeight: "bold" },
  modalCloseText: { fontSize: 20 },
  modalContent: { padding: 16 },
  detailName: { fontSize: 24, fontWeight: "bold", marginBottom: 16 },
  section: { marginBottom: 24 },
  sectionTitle: { fontSize: 18, fontWeight: "600", marginBottom: 12 },
  text: { fontSize: 16, lineHeight: 24, color: "#333" },
  exerciseDetailCard: {
    backgroundColor: "#f8f8f8",
    padding: 12,
    borderRadius: 8,
    marginBottom: 12,
  },
  exerciseDetailHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  exerciseDetailImage: {
    width: 60,
    height: 60,
    borderRadius: 8,
    marginRight: 12,
  },
  exerciseDetailName: { fontSize: 16, fontWeight: "600", flex: 1 },
  setsContainer: { marginTop: 8 },
  setsTitle: {
    fontSize: 14,
    fontWeight: "600",
    color: "#666",
    marginBottom: 6,
  },
  setRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    backgroundColor: "#fff",
    padding: 8,
    borderRadius: 6,
    marginBottom: 4,
  },
  setNumber: { fontSize: 14, color: "#666" },
  setInfo: { fontSize: 14, fontWeight: "500", color: "#2B7A78" },
  actionButtonsContainer: {
    gap: 12,
    marginTop: 16,
  },
  startWorkoutButton: {
    backgroundColor: "#2B7A78",
    padding: 18,
    borderRadius: 8,
    alignItems: "center",
  },
  startWorkoutButtonText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "bold",
  },
  editButton: {
    backgroundColor: "#3AAFA9",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
  },
  editButtonText: { color: "#fff", fontSize: 16, fontWeight: "600" },
  deleteButton: {
    backgroundColor: "#ff3b30",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
  },
  deleteButtonText: { color: "#fff", fontSize: 16, fontWeight: "600" },
  loadingText: { marginTop: 12, color: "#666" },
  errorText: { color: "#ff3b30", marginBottom: 16, fontSize: 16 },
  retryButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  retryButtonText: { color: "#fff", fontSize: 16 },
});
