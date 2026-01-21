import { useRouter } from "expo-router";
import { Eye, EyeOff, Lock, Mail, User } from "lucide-react-native";
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
import apiClient from "../services/apiClient";
import { saveToken } from "../services/authStorage";

export default function Signup() {
  const [showPassword, setShowPassword] = useState(false);
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const navigateToLogin = () => router.push("/auth/Login");
  const navigateToCompleteProfile = () =>
    router.replace("/auth/CompleteProfile");

  const handleSignup = async () => {
    if (!fullName || !email || !password) {
      Alert.alert("Error", "Please fill in all fields");
      return;
    }

    setLoading(true);

    const requestData = {
      fullName: fullName,
      email: email,
      password: password,
    };

    try {
      const response = await apiClient.post("/api/users/signup", requestData);
      await saveToken(response.data.token);
      Alert.alert("Success", "Account created successfully!");
      navigateToCompleteProfile();
    } catch (error: any) {
      let message = "Failed to create account";
      if (error.response && error.response.data) {
        message =
          error.response.data.message || JSON.stringify(error.response.data);
      }
      Alert.alert("Error", message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.title}>Creating account</Text>
          <Text style={styles.subtitle}>
            Creating an account is required to continue
          </Text>
        </View>

        <View style={styles.form}>
          {/* Name */}
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Name and Last Name</Text>
            <View style={styles.inputContainer}>
              <User color="#015551" size={22} style={styles.icon} />
              <TextInput
                style={styles.input}
                placeholder="Name and Last Name"
                placeholderTextColor="#015551"
                value={fullName}
                onChangeText={setFullName}
              />
            </View>
          </View>

          {/* Email */}
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Email</Text>
            <View style={styles.inputContainer}>
              <Mail color="#015551" size={22} style={styles.icon} />
              <TextInput
                style={styles.input}
                placeholder="Email"
                placeholderTextColor="#015551"
                keyboardType="email-address"
                autoCapitalize="none"
                value={email}
                onChangeText={setEmail}
              />
            </View>
          </View>

          {/* Password */}
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Password</Text>
            <View style={styles.inputContainer}>
              <Lock color="#015551" size={22} style={styles.icon} />
              <TextInput
                style={styles.input}
                placeholder="Password"
                placeholderTextColor="#015551"
                secureTextEntry={!showPassword}
                autoCapitalize="none"
                value={password}
                onChangeText={setPassword}
              />
              <TouchableOpacity
                onPress={() => setShowPassword(!showPassword)}
                style={styles.eyeIcon}
              >
                {showPassword ? (
                  <EyeOff color="#015551" size={22} />
                ) : (
                  <Eye color="#015551" size={22} />
                )}
              </TouchableOpacity>
            </View>
          </View>

          <TouchableOpacity
            style={styles.button}
            onPress={handleSignup}
            disabled={loading}
          >
            <Text style={styles.buttonText}>
              {loading ? "Creating account..." : "Sign up"}
            </Text>
          </TouchableOpacity>

          <View style={styles.loginContainer}>
            <Text style={styles.loginText}>If you have an account</Text>
            <TouchableOpacity onPress={navigateToLogin}>
              <Text style={styles.loginLink}>Log in</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: "center",
    paddingHorizontal: 30, // Replaces marginLeft: 50 for better centering
    paddingVertical: 40,
  },
  header: {
    marginBottom: 30,
  },
  title: {
    fontFamily: "SFProDisplay-Bold",
    fontSize: 32,
    fontWeight: "bold",
    color: "#015551",
    marginBottom: 8,
  },
  subtitle: {
    fontFamily: "SFProDisplay-Bold",
    fontWeight: "600",
    fontSize: 14,
    color: "#015551",
    opacity: 0.8,
  },
  form: {
    width: "100%",
  },
  inputGroup: {
    marginBottom: 20,
  },
  label: {
    fontFamily: "SFProDisplay-Bold",
    fontSize: 16,
    fontWeight: "bold",
    color: "#015551",
    marginBottom: 8,
    marginLeft: 4,
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1.5, // Slightly thicker for a premium feel
    borderColor: "#015551",
    borderRadius: 16, // Clean curve, not too round
    height: 56, // Modern height
    paddingHorizontal: 15,
    backgroundColor: "#fff",
    // Subtle shadow for depth
    shadowColor: "#015551",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  icon: {
    marginRight: 10,
  },
  input: {
    flex: 1,
    fontSize: 16,
    fontWeight: "600",
    color: "#015551",
    height: "100%", // Ensures full clickability
  },
  eyeIcon: {
    padding: 5,
  },
  button: {
    backgroundColor: "#015551",
    borderRadius: 16,
    height: 56,
    justifyContent: "center",
    alignItems: "center",
    marginTop: 10,
    shadowColor: "#015551",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 8,
    elevation: 4,
  },
  buttonText: {
    color: "#FFFFFF",
    fontSize: 18,
    fontWeight: "bold",
  },
  loginContainer: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: 25,
  },
  loginText: {
    color: "#015551",
    fontSize: 14,
    marginRight: 5,
  },
  loginLink: {
    color: "#015551",
    fontSize: 14,
    fontWeight: "bold",
    textDecorationLine: "underline",
  },
});
