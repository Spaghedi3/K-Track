import apiClient from "@/app/services/apiClient";

import * as ImagePicker from "expo-image-picker";
import { useState } from "react";
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

const CLOUDINARY_CLOUD_NAME =
  process.env.EXPO_PUBLIC_CLOUDINARY_CLOUD_NAME ?? "";

const CLOUDINARY_UPLOAD_PRESET =
  process.env.EXPO_PUBLIC_CLOUDINARY_UPLOAD_PRESET ?? "";

// const CLOUDINARY_CLOUD_NAME = "ddkbbstkm";
// const CLOUDINARY_UPLOAD_PRESET = "ktrack";

/* ---------------- HARD CODED OPTIONS ---------------- */

const MUSCLE_OPTIONS = [
  "shins",
  "hands",
  "sternocleidomastoid",
  "soleus",
  "inner thighs",
  "lower abs",
  "grip muscles",
  "abdominals",
  "wrist extensors",
  "wrist flexors",
  "latissimus dorsi",
  "upper chest",
  "rotator cuff",
  "wrists",
  "groin",
  "brachialis",
  "deltoids",
  "feet",
  "ankles",
  "trapezius",
  "rear deltoids",
  "chest",
  "quadriceps",
  "back",
  "core",
  "shoulders",
  "ankle stabilizers",
  "rhomboids",
  "obliques",
  "lower back",
  "hip flexors",
  "levator scapulae",
  "abductors",
  "serratus anterior",
  "traps",
  "forearms",
  "delts",
  "biceps",
  "upper back",
  "spine",
  "cardiovascular system",
  "triceps",
  "adductors",
  "hamstrings",
  "glutes",
  "pectorals",
  "calves",
  "lats",
  "quads",
  "abs",
].map((m) => ({ label: m, value: m }));

const BODY_PART_OPTIONS = [
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
].map((b) => ({ label: b, value: b }));

const EQUIPMENT_OPTIONS = [
  "stepmill machine",
  "elliptical machine",
  "trap bar",
  "tire",
  "stationary bike",
  "wheel roller",
  "smith machine",
  "hammer",
  "skierg machine",
  "roller",
  "resistance band",
  "bosu ball",
  "weighted",
  "olympic barbell",
  "kettlebell",
  "upper body ergometer",
  "sled machine",
  "ez barbell",
  "dumbbell",
  "rope",
  "barbell",
  "band",
  "stability ball",
  "medicine ball",
  "assisted",
  "leverage machine",
  "cable",
  "body weight",
].map((e) => ({ label: e, value: e }));

/* ---------------- TYPES ---------------- */

interface Props {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

/* ---------------- COMPONENT ---------------- */

export default function AddExerciseModal({
  visible,
  onClose,
  onSuccess,
}: Props) {
  const [loading, setLoading] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);

  const [newExercise, setNewExercise] = useState({
    name: "",
    imageUrl: "",
    overview: "",
    equipments: [] as string[],
    bodyParts: [] as string[],
    targetMuscles: [] as string[],
    secondaryMuscles: [] as string[],
    instructions: "",
  });

  /* ---------------- IMAGE PICKER ---------------- */

  const pickImage = async () => {
    try {
      // Request permission
      const { status } =
        await ImagePicker.requestMediaLibraryPermissionsAsync();

      if (status !== "granted") {
        Alert.alert(
          "Permission needed",
          "Please grant camera roll permissions to upload images.",
        );
        return;
      }

      // Launch image picker
      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ImagePicker.MediaTypeOptions.Images,
        allowsEditing: true,
        aspect: [4, 3],
        quality: 0.8,
      });

      if (!result.canceled && result.assets[0]) {
        uploadToCloudinary(result.assets[0].uri);
      }
    } catch (error) {
      console.error("Error picking image:", error);
      Alert.alert("Error", "Failed to pick image");
    }
  };

  /* ---------------- CLOUDINARY UPLOAD ---------------- */

  const uploadToCloudinary = async (uri: string) => {
    try {
      setUploadingImage(true);

      // Create form data
      const formData = new FormData();
      formData.append("file", {
        uri,
        type: "image/jpeg",
        name: "exercise-image.jpg",
      } as any);
      formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);

      // Upload to Cloudinary
      const response = await fetch(
        `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
        {
          method: "POST",
          body: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      const data = await response.json();

      if (data.secure_url) {
        setNewExercise({ ...newExercise, imageUrl: data.secure_url });
        Alert.alert("Success", "Image uploaded successfully!");
      } else {
        throw new Error("No URL returned from Cloudinary");
      }
    } catch (error) {
      console.error("Error uploading to Cloudinary:", error);
      Alert.alert("Upload failed", "Could not upload image. Please try again.");
    } finally {
      setUploadingImage(false);
    }
  };

  /* ---------------- HANDLERS ---------------- */

  const handleAddExercise = async () => {
    try {
      setLoading(true);

      const payload = {
        name: newExercise.name,
        imageUrl: newExercise.imageUrl || null,
        overview: newExercise.overview || null,
        equipments: newExercise.equipments,
        bodyParts: newExercise.bodyParts,
        targetMuscles: newExercise.targetMuscles,
        secondaryMuscles: newExercise.secondaryMuscles,
        instructions: splitLines(newExercise.instructions),
        keywords: [],
        exerciseTips: [],
        variations: [],
        relatedExerciseIds: [],
      };

      await apiClient.post("/api/exercises/custom", payload);

      // Reset form
      setNewExercise({
        name: "",
        imageUrl: "",
        overview: "",
        equipments: [],
        bodyParts: [],
        targetMuscles: [],
        secondaryMuscles: [],
        instructions: "",
      });

      onClose();
      onSuccess();
    } catch (err) {
      console.error("Failed to add exercise:", err);
      Alert.alert("Error", "Failed to add exercise");
    } finally {
      setLoading(false);
    }
  };

  const removeImage = () => {
    setNewExercise({ ...newExercise, imageUrl: "" });
  };

  return (
    <Modal visible={visible} animationType="slide">
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.title}>Add Custom Exercise</Text>
          <TouchableOpacity onPress={onClose} style={styles.closeButton}>
            <Text style={styles.close}>✕</Text>
          </TouchableOpacity>
        </View>

        <ScrollView contentContainerStyle={styles.content}>
          {/* NAME */}
          <View style={styles.group}>
            <Text style={styles.label}>Name *</Text>
            <TextInput
              style={styles.input}
              value={newExercise.name}
              onChangeText={(v) => setNewExercise({ ...newExercise, name: v })}
              placeholder="Exercise name"
            />
          </View>

          {/* IMAGE UPLOAD */}
          <View style={styles.group}>
            <Text style={styles.label}>Exercise Image</Text>

            {newExercise.imageUrl ? (
              <View style={styles.imagePreviewContainer}>
                <Image
                  source={{ uri: newExercise.imageUrl }}
                  style={styles.imagePreview}
                  resizeMode="cover"
                />
                <TouchableOpacity
                  style={styles.removeImageButton}
                  onPress={removeImage}
                >
                  <Text style={styles.removeImageText}>✕</Text>
                </TouchableOpacity>
              </View>
            ) : (
              <TouchableOpacity
                style={styles.uploadButton}
                onPress={pickImage}
                disabled={uploadingImage}
              >
                {uploadingImage ? (
                  <ActivityIndicator color="#015551" />
                ) : (
                  <>
                    <Text style={styles.uploadIcon}>📷</Text>
                    <Text style={styles.uploadText}>
                      Select Image from Gallery
                    </Text>
                  </>
                )}
              </TouchableOpacity>
            )}
          </View>

          {/* OVERVIEW */}
          <View style={styles.group}>
            <Text style={styles.label}>Overview</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              value={newExercise.overview}
              onChangeText={(v) =>
                setNewExercise({ ...newExercise, overview: v })
              }
              placeholder="Brief description"
              multiline
              numberOfLines={3}
            />
          </View>

          {/* TARGET MUSCLES */}
          <View style={styles.group}>
            <Text style={styles.label}>Target Muscles *</Text>
            <ModernDropdownMultiselect
              options={MUSCLE_OPTIONS}
              value={newExercise.targetMuscles}
              onValuesChange={(values) =>
                setNewExercise({ ...newExercise, targetMuscles: values })
              }
              placeholder="Select target muscles"
              multiSelect
            />
          </View>

          {/* SECONDARY MUSCLES */}
          <View style={styles.group}>
            <Text style={styles.label}>Secondary Muscles</Text>
            <ModernDropdownMultiselect
              options={MUSCLE_OPTIONS}
              value={newExercise.secondaryMuscles}
              onValuesChange={(values) =>
                setNewExercise({ ...newExercise, secondaryMuscles: values })
              }
              placeholder="Select secondary muscles"
              multiSelect
            />
          </View>

          {/* BODY PARTS */}
          <View style={styles.group}>
            <Text style={styles.label}>Body Parts *</Text>
            <ModernDropdownMultiselect
              options={BODY_PART_OPTIONS}
              value={newExercise.bodyParts}
              onValuesChange={(values) =>
                setNewExercise({ ...newExercise, bodyParts: values })
              }
              placeholder="Select body parts"
              multiSelect
            />
          </View>

          {/* EQUIPMENT */}
          <View style={styles.group}>
            <Text style={styles.label}>Equipment</Text>
            <ModernDropdownMultiselect
              options={EQUIPMENT_OPTIONS}
              value={newExercise.equipments}
              onValuesChange={(values) =>
                setNewExercise({ ...newExercise, equipments: values })
              }
              placeholder="Select equipment"
              multiSelect
            />
          </View>

          {/* INSTRUCTIONS */}
          <View style={styles.group}>
            <Text style={styles.label}>Instructions (one per line)</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              value={newExercise.instructions}
              onChangeText={(v) =>
                setNewExercise({ ...newExercise, instructions: v })
              }
              placeholder="Step 1: ...&#10;Step 2: ..."
              multiline
              numberOfLines={6}
            />
          </View>

          {/* SUBMIT BUTTON */}
          <TouchableOpacity
            style={[
              styles.submit,
              (!newExercise.name || loading) && styles.disabled,
            ]}
            onPress={handleAddExercise}
            disabled={!newExercise.name || loading}
          >
            <Text style={styles.submitText}>
              {loading ? "Adding..." : "Add Exercise"}
            </Text>
          </TouchableOpacity>
        </ScrollView>
      </View>
    </Modal>
  );
}

/* ---------------- HELPERS ---------------- */

const splitLines = (value: string) =>
  value
    .split("\n")
    .map((s) => s.trim())
    .filter(Boolean);

/* ---------------- STYLES ---------------- */

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
  group: { marginBottom: 20 },
  label: { fontSize: 16, fontWeight: "600", marginBottom: 8, color: "#333" },
  input: {
    backgroundColor: "#f0f0f0",
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
  },
  textArea: { minHeight: 100, textAlignVertical: "top" },

  /* IMAGE UPLOAD */
  uploadButton: {
    backgroundColor: "#f0f0f0",
    borderWidth: 2,
    borderColor: "#3AAFA9",
    borderStyle: "dashed",
    borderRadius: 12,
    padding: 32,
    alignItems: "center",
    justifyContent: "center",
    minHeight: 150,
  },
  uploadIcon: {
    fontSize: 48,
    marginBottom: 8,
  },
  uploadText: {
    fontSize: 16,
    color: "#015551",
    fontWeight: "600",
  },
  imagePreviewContainer: {
    position: "relative",
    borderRadius: 12,
    overflow: "hidden",
    backgroundColor: "#f0f0f0",
  },
  imagePreview: {
    width: "100%",
    height: 200,
  },
  removeImageButton: {
    position: "absolute",
    top: 8,
    right: 8,
    backgroundColor: "rgba(0, 0, 0, 0.6)",
    width: 32,
    height: 32,
    borderRadius: 16,
    justifyContent: "center",
    alignItems: "center",
  },
  removeImageText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "bold",
  },

  /* SUBMIT */
  submit: {
    backgroundColor: "#3AAFA9",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 24,
  },
  disabled: { backgroundColor: "#9ADAD6" },
  submitText: { color: "#fff", fontSize: 18, fontWeight: "600" },
});
