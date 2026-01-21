import { useRouter } from "expo-router";
import React, { useState } from "react";
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import ModernDropdown from "../componets/ModernDropdown";
import apiClient from "../services/apiClient";

export default function CompleteProfile() {
  const router = useRouter();

  const [age, setAge] = useState("");
  const [gender, setGender] = useState("");
  const [height, setHeight] = useState("");
  const [weight, setWeight] = useState("");
  const [activityLevel, setActivityLevel] = useState("");
  const [goal, setGoal] = useState("");
  const [loading, setLoading] = useState(false);

  const genderOptions = [
    { label: "Male", value: "MALE" },
    { label: "Female", value: "FEMALE" },
    { label: "Other", value: "OTHER" },
  ];

  const activityLevelOptions = [
    { label: "Low (1-2 days/week)", value: "LOW" },
    { label: "Medium (3-5 days/week)", value: "MEDIUM" },
    { label: "High (6-7 days/week)", value: "HIGH" },
  ];

  const goalOptions = [
    { label: "Lose Weight", value: "LOSE_WEIGHT" },
    { label: "Maintain Weight", value: "MAINTAIN_WEIGHT" },
    { label: "Gain Muscle", value: "GAIN_MUSCLE" },
  ];

  const handleCompleteProfile = async () => {
    setLoading(true);

    try {
      await apiClient.put("/api/users/complete-profile", {
        age: age ? Number(age) : null,
        gender: gender || null,
        height: height ? Number(height) : null,
        weight: weight ? Number(weight) : null,
        activityLevel: activityLevel || null,
        goal: goal || null,
      });

      Alert.alert("Success", "Profile completed!");
      router.replace("/home");
    } catch (error: any) {
      Alert.alert("Error", "Failed to complete profile");
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.screen}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.container}>
          <Text style={styles.title}>Complete your profile</Text>
          <Text style={styles.subtitle}>
            Help us personalize your fitness journey
          </Text>

          <View style={styles.form}>
            <View style={styles.row}>
              <View style={styles.halfWidth}>
                <Text style={styles.label}>Age</Text>
                <TextInput
                  style={styles.input}
                  placeholder="25"
                  placeholderTextColor="#a0a0a0"
                  keyboardType="numeric"
                  value={age}
                  onChangeText={setAge}
                />
              </View>

              <View style={styles.halfWidth}>
                <Text style={styles.label}>Gender</Text>
                <ModernDropdown
                  options={genderOptions}
                  value={gender}
                  onValueChange={setGender}
                  placeholder="Select gender"
                />
              </View>
            </View>

            <View style={styles.row}>
              <View style={styles.halfWidth}>
                <Text style={styles.label}>Height (cm)</Text>
                <TextInput
                  style={styles.input}
                  placeholder="175"
                  placeholderTextColor="#a0a0a0"
                  keyboardType="numeric"
                  value={height}
                  onChangeText={setHeight}
                />
              </View>

              <View style={styles.halfWidth}>
                <Text style={styles.label}>Weight (kg)</Text>
                <TextInput
                  style={styles.input}
                  placeholder="70"
                  placeholderTextColor="#a0a0a0"
                  keyboardType="numeric"
                  value={weight}
                  onChangeText={setWeight}
                />
              </View>
            </View>

            <Text style={styles.label}>Activity Level</Text>
            <ModernDropdown
              options={activityLevelOptions}
              value={activityLevel}
              onValueChange={setActivityLevel}
              placeholder="Select your activity level"
            />

            <Text style={styles.label}>Fitness Goal</Text>
            <ModernDropdown
              options={goalOptions}
              value={goal}
              onValueChange={setGoal}
              placeholder="Select your goal"
            />

            <TouchableOpacity
              style={[styles.button, loading && styles.buttonDisabled]}
              onPress={handleCompleteProfile}
              disabled={loading}
            >
              <Text style={styles.buttonText}>
                {loading ? "Saving..." : "Continue"}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#f8faf9",
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: "center",
    paddingVertical: 40,
  },
  container: {
    paddingHorizontal: 30,
  },
  title: {
    fontSize: 32,
    fontWeight: "bold",
    color: "#015551",
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: "#666",
    marginBottom: 30,
  },
  form: {
    width: "100%",
    maxWidth: 500,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 15,
    marginBottom: 5,
  },
  halfWidth: {
    flex: 1,
  },
  label: {
    fontSize: 15,
    fontWeight: "600",
    color: "#015551",
    marginBottom: 8,
    marginTop: 12,
  },
  input: {
    borderWidth: 1.5,
    borderColor: "#e0e0e0",
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: "#015551",
    backgroundColor: "#fff",
  },
  button: {
    backgroundColor: "#015551",
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: "center",
    marginTop: 30,
    shadowColor: "#015551",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 5,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "bold",
  },
});
