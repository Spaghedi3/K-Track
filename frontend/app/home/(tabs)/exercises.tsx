import AddExerciseModal from "@/app/componets/AddExerciseModal";
import apiClient from "@/app/services/apiClient";
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

interface Exercise {
  id: number;
  exerciseId: string;
  name: string;
  imageUrl?: string;
  videoUrl?: string;
  overview?: string;
  equipments: string[];
  bodyParts: string[];
  targetMuscles: string[];
  secondaryMuscles: string[];
  keywords: string[];
  instructions: string[];
  exerciseTips: string[];
  variations: string[];
  relatedExerciseIds: string[];
  type: string;
}

type TabType = "preloaded" | "custom";

export default function ExercisesTab() {
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedExercise, setSelectedExercise] = useState<Exercise | null>(
    null,
  );
  const [activeTab, setActiveTab] = useState<TabType>("preloaded");
  const [showAddModal, setShowAddModal] = useState(false);

  const [selectedEquipment, setSelectedEquipment] = useState<string>("All");
  const [selectedBodyPart, setSelectedBodyPart] = useState<string>("All");
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    fetchExercises();
    setSearchQuery("");
    setSelectedEquipment("All");
    setSelectedBodyPart("All");
  }, [activeTab]);

  const fetchExercises = async () => {
    try {
      setLoading(true);

      const endpoint =
        activeTab === "preloaded"
          ? "/api/exercises/preloaded"
          : "/api/exercises/custom";

      const response = await apiClient.get(endpoint);
      setExercises(response.data);
      setError(null);
    } catch (err) {
      console.error(err);
      setError("Failed to load exercises");
    } finally {
      setLoading(false);
    }
  };

  // ---------- DELETE FUNCTION ----------
  const handleDeleteExercise = (id: number) => {
    Alert.alert(
      "Delete Exercise",
      "Are you sure you want to delete this exercise?",
      [
        { text: "Cancel", style: "cancel" },
        {
          text: "Delete",
          style: "destructive",
          onPress: async () => {
            try {
              await apiClient.delete(`/api/exercises/custom/${id}`);
              Alert.alert("Deleted", "Exercise deleted successfully");
              fetchExercises();
            } catch (err) {
              console.error(err);
              Alert.alert("Error", "Failed to delete exercise");
            }
          },
        },
      ],
    );
  };

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch =
      exercise.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      exercise.targetMuscles.some((m) =>
        m.toLowerCase().includes(searchQuery.toLowerCase()),
      ) ||
      exercise.bodyParts.some((b) =>
        b.toLowerCase().includes(searchQuery.toLowerCase()),
      );

    const matchesEquipment =
      selectedEquipment === "All" ||
      exercise.equipments.some((e) => e === selectedEquipment);

    const matchesBodyPart =
      selectedBodyPart === "All" ||
      exercise.bodyParts.some((b) => b === selectedBodyPart);

    return matchesSearch && matchesEquipment && matchesBodyPart;
  });

  const uniqueEquipments = [
    "All",
    ...Array.from(new Set(exercises.flatMap((e) => e.equipments))).sort(),
  ];
  const uniqueBodyParts = [
    "All",
    "neck",
    "lower arms",
    "shoulders",
    "cardio",
    "upper arms",
    "chest",
    "lower legs",
    "back",
    "upper legs",
    "waist",
  ];

  const renderExerciseItem = ({ item }: { item: Exercise }) => (
    <TouchableOpacity
      style={styles.exerciseCard}
      onPress={() => setSelectedExercise(item)}
    >
      {item.imageUrl && (
        <Image
          source={{ uri: item.imageUrl }}
          style={styles.exerciseImage}
          resizeMode="contain"
        />
      )}

      <View style={styles.exerciseInfo}>
        <Text style={styles.exerciseName}>{item.name}</Text>

        <View style={styles.muscleContainer}>
          {item.targetMuscles.map((muscle, index) => (
            <View key={index} style={styles.muscleTag}>
              <Text style={styles.muscleText}>{muscle}</Text>
            </View>
          ))}
        </View>

        <Text style={styles.equipmentText}>
          {item.equipments.join(", ") || "No equipment"}
        </Text>

        {/* DELETE BUTTON ONLY SHOWS ON CUSTOM TAB */}
        {activeTab === "custom" && (
          <TouchableOpacity
            style={styles.deleteButton}
            onPress={() => handleDeleteExercise(item.id)}
          >
            <Text style={styles.deleteText}>Delete</Text>
          </TouchableOpacity>
        )}
      </View>
    </TouchableOpacity>
  );

  const renderExerciseDetail = () => {
    if (!selectedExercise) return null;

    return (
      <Modal visible animationType="slide">
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Exercise Details</Text>
            <TouchableOpacity onPress={() => setSelectedExercise(null)}>
              <Text style={styles.modalCloseText}>✕</Text>
            </TouchableOpacity>
          </View>

          <ScrollView style={styles.modalContent}>
            {selectedExercise.imageUrl && (
              <Image
                source={{ uri: selectedExercise.imageUrl }}
                style={styles.detailImage}
                resizeMode="contain"
              />
            )}

            <Text style={styles.detailName}>{selectedExercise.name}</Text>

            {selectedExercise.overview && (
              <View style={styles.section}>
                <Text style={styles.sectionTitle}>Overview</Text>
                <Text style={styles.text}>{selectedExercise.overview}</Text>
              </View>
            )}

            <View style={styles.section}>
              <Text style={styles.sectionTitle}>Target Muscles</Text>
              <View style={styles.tagContainer}>
                {selectedExercise.targetMuscles.map((m, i) => (
                  <View key={i} style={styles.tag}>
                    <Text style={styles.tagText}>{m}</Text>
                  </View>
                ))}
              </View>
            </View>

            {selectedExercise.secondaryMuscles.length > 0 && (
              <View style={styles.section}>
                <Text style={styles.sectionTitle}>Secondary Muscles</Text>
                <View style={styles.tagContainer}>
                  {selectedExercise.secondaryMuscles.map((m, i) => (
                    <View key={i} style={styles.secondaryTag}>
                      <Text style={styles.tagText}>{m}</Text>
                    </View>
                  ))}
                </View>
              </View>
            )}

            <View style={styles.section}>
              <Text style={styles.sectionTitle}>Equipment</Text>
              <Text style={styles.text}>
                {selectedExercise.equipments.join(", ") || "None"}
              </Text>
            </View>

            {selectedExercise.instructions.length > 0 && (
              <View style={styles.section}>
                <Text style={styles.sectionTitle}>Instructions</Text>
                {selectedExercise.instructions.map((step, i) => (
                  <Text key={i} style={styles.instruction}>
                    {step}
                  </Text>
                ))}
              </View>
            )}
          </ScrollView>
        </View>
      </Modal>
    );
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#007AFF" />
        <Text style={styles.loadingText}>Loading exercises...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>{error}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={fetchExercises}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Exercises</Text>

        <View style={styles.tabContainer}>
          <TouchableOpacity
            style={[
              styles.tabButton,
              activeTab === "preloaded" && styles.tabButtonActive,
            ]}
            onPress={() => setActiveTab("preloaded")}
          >
            <Text
              style={[
                styles.tabButtonText,
                activeTab === "preloaded" && styles.tabButtonTextActive,
              ]}
            >
              Preloaded
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.tabButton,
              activeTab === "custom" && styles.tabButtonActive,
            ]}
            onPress={() => setActiveTab("custom")}
          >
            <Text
              style={[
                styles.tabButtonText,
                activeTab === "custom" && styles.tabButtonTextActive,
              ]}
            >
              Custom
            </Text>
          </TouchableOpacity>
        </View>

        <TextInput
          style={styles.searchInput}
          placeholder="Search exercises, muscles, or body parts..."
          value={searchQuery}
          onChangeText={setSearchQuery}
          placeholderTextColor="#999"
        />

        <TouchableOpacity
          style={styles.filterToggle}
          onPress={() => setShowFilters(!showFilters)}
        >
          <Text style={styles.filterToggleText}>
            {showFilters ? "Hide Filters" : "Show Filters"}
          </Text>
        </TouchableOpacity>

        {showFilters && (
          <View style={styles.filtersContainer}>
            <View style={styles.filterGroup}>
              <Text style={styles.filterLabel}>Equipment:</Text>
              <ScrollView
                horizontal
                showsHorizontalScrollIndicator={false}
                style={styles.filterScroll}
              >
                {uniqueEquipments.map((equipment) => (
                  <TouchableOpacity
                    key={equipment}
                    style={[
                      styles.filterChip,
                      selectedEquipment === equipment &&
                        styles.filterChipActive,
                    ]}
                    onPress={() => setSelectedEquipment(equipment)}
                  >
                    <Text
                      style={[
                        styles.filterChipText,
                        selectedEquipment === equipment &&
                          styles.filterChipTextActive,
                      ]}
                    >
                      {equipment}
                    </Text>
                  </TouchableOpacity>
                ))}
              </ScrollView>
            </View>

            <View style={styles.filterGroup}>
              <Text style={styles.filterLabel}>Body Part:</Text>
              <ScrollView
                horizontal
                showsHorizontalScrollIndicator={false}
                style={styles.filterScroll}
              >
                {uniqueBodyParts.map((bodyPart) => (
                  <TouchableOpacity
                    key={bodyPart}
                    style={[
                      styles.filterChip,
                      selectedBodyPart === bodyPart && styles.filterChipActive,
                    ]}
                    onPress={() => setSelectedBodyPart(bodyPart)}
                  >
                    <Text
                      style={[
                        styles.filterChipText,
                        selectedBodyPart === bodyPart &&
                          styles.filterChipTextActive,
                      ]}
                    >
                      {bodyPart}
                    </Text>
                  </TouchableOpacity>
                ))}
              </ScrollView>
            </View>
          </View>
        )}
      </View>

      <FlatList
        data={filteredExercises}
        renderItem={renderExerciseItem}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.listContainer}
      />

      <TouchableOpacity
        style={styles.fab}
        onPress={() => setShowAddModal(true)}
      >
        <Text style={styles.fabText}>+</Text>
      </TouchableOpacity>

      {renderExerciseDetail()}

      <AddExerciseModal
        visible={showAddModal}
        onClose={() => setShowAddModal(false)}
        onSuccess={fetchExercises}
      />
    </View>
  );
}

/* ===== styles (unchanged) ===== */
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
  tabContainer: {
    flexDirection: "row",
    marginBottom: 16,
    backgroundColor: "#f0f0f0",
    borderRadius: 8,
    padding: 4,
  },
  tabButton: { flex: 1, paddingVertical: 8, alignItems: "center" },
  tabButtonActive: { backgroundColor: "#3AAFA9", borderRadius: 6 },
  tabButtonText: { fontSize: 16, color: "#2B7A78" },
  tabButtonTextActive: { color: "#fff" },
  searchInput: {
    backgroundColor: "#f0f0f0",
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    color: "#000",
  },
  filterToggle: {
    marginTop: 12,
    padding: 10,
    backgroundColor: "#3AAFA9",
    borderRadius: 8,
    alignItems: "center",
  },
  filterToggleText: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "600",
  },
  filtersContainer: {
    marginTop: 12,
    backgroundColor: "#e8e8e8",
    padding: 12,
    borderRadius: 8,
  },
  filterGroup: {
    marginBottom: 12,
  },
  filterLabel: {
    fontSize: 14,
    fontWeight: "600",
    color: "#2B7A78",
    marginBottom: 8,
  },
  filterScroll: {
    flexDirection: "row",
  },
  filterChip: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    backgroundColor: "#e0e0e0",
    borderRadius: 20,
    marginRight: 8,
  },
  filterChipActive: {
    backgroundColor: "#3AAFA9",
  },
  filterChipText: {
    fontSize: 13,
    color: "#666",
  },
  filterChipTextActive: {
    color: "#fff",
    fontWeight: "600",
  },
  listContainer: { padding: 16 },
  exerciseCard: {
    backgroundColor: "#fff",
    borderRadius: 12,
    marginBottom: 12,
    overflow: "hidden",
  },
  exerciseImage: { width: "100%", height: 200 },
  exerciseInfo: { padding: 16 },
  exerciseName: { fontSize: 18, fontWeight: "600", marginBottom: 8 },
  muscleContainer: { flexDirection: "row", flexWrap: "wrap" },
  muscleTag: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 16,
    marginRight: 6,
    marginBottom: 6,
  },
  muscleText: { color: "#fff", fontSize: 12 },
  equipmentText: { color: "#666" },
  deleteButton: {
    marginTop: 10,
    backgroundColor: "#ff4d4d",
    padding: 10,
    borderRadius: 8,
    alignItems: "center",
  },
  deleteText: { color: "#fff", fontWeight: "bold" },
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
  fabText: { color: "#fff", fontSize: 32, lineHeight: 32 },
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
  detailImage: { width: "100%", height: 300 },
  detailName: { fontSize: 24, fontWeight: "bold", marginBottom: 16 },
  section: { marginBottom: 20 },
  sectionTitle: { fontSize: 18, fontWeight: "600", marginBottom: 8 },
  tagContainer: { flexDirection: "row", flexWrap: "wrap" },
  tag: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 16,
    marginRight: 6,
    marginBottom: 6,
  },
  secondaryTag: {
    backgroundColor: "#2B7A78",
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 16,
    marginRight: 6,
    marginBottom: 6,
  },
  tagText: { color: "#fff" },
  text: { fontSize: 16, lineHeight: 24 },
  instruction: { fontSize: 15, marginBottom: 6 },
  loadingText: { marginTop: 12 },
  errorText: { color: "#ff3b30", marginBottom: 16 },
  retryButton: {
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  retryButtonText: { color: "#fff", fontSize: 16 },
});
