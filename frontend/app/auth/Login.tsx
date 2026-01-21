import { useRouter } from "expo-router";
import { Eye, EyeOff, Lock, Mail } from "lucide-react-native";
import React, { useState } from "react";
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import apiClient from "../services/apiClient";
import { saveToken } from "../services/authStorage";

export default function Login() {
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const navigateToSignup = () => router.push("/auth/Signup");
  const navigateToHome = () => router.replace("/home/(tabs)");

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert("Error", "Please fill in all fields");
      return;
    }

    setLoading(true);

    const requestData = {
      email: email,
      password: password,
    };

    try {
      console.log(apiClient.defaults.baseURL + "/api/users/login");
      const response = await apiClient.post("/api/users/login", requestData);
      await saveToken(response.data.token);
      console.log("Response:", response.data);

      Alert.alert("Success", "Logged in successfully!", [
        {
          text: "OK",
          onPress: () => navigateToHome(),
        },
      ]);
    } catch (error: any) {
      console.error("Error details:", error);

      let message = "Failed to login";

      if (error.response) {
        console.log("Error response:", error.response.data);
        console.log("Error status:", error.response.status);

        if (error.response.data && error.response.data.message) {
          message = error.response.data.message;
        } else if (error.response.status === 401) {
          message = "Invalid email or password";
        } else if (error.response.status === 400) {
          message = "Invalid data. Please check your inputs.";
        } else if (error.response.data) {
          message =
            typeof error.response.data === "string"
              ? error.response.data
              : JSON.stringify(error.response.data);
        }
      } else if (error.request) {
        console.log("No response received:", error.request);
        message = "No response from server. Please check your connection.";
      } else {
        console.log("Error:", error.message);
        message = error.message || "An unexpected error occurred";
      }

      Alert.alert("Error", message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.screen}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <View>
        <Text style={styles.title}>Sign in</Text>
        <Text style={styles.subtitle}>
          To proceed with your account you must sign in.
        </Text>

        <View style={styles.form}>
          {/* Email */}
          <Text style={styles.label}>Email</Text>
          <View style={styles.inputContainer}>
            <Mail color="#015551" size={20} style={styles.icon} />
            <TextInput
              style={styles.input}
              placeholder="Enter your email"
              placeholderTextColor="rgba(1, 85, 81, 0.5)"
              keyboardType="email-address"
              autoCapitalize="none"
              value={email}
              onChangeText={setEmail}
              editable={!loading}
            />
          </View>

          {/* Password */}
          <Text style={styles.label}>Password</Text>
          <View style={styles.inputContainer}>
            <Lock color="#015551" size={20} style={styles.icon} />
            <TextInput
              style={styles.input}
              placeholder="Enter your password"
              placeholderTextColor="rgba(1, 85, 81, 0.5)"
              secureTextEntry={!showPassword}
              autoCapitalize="none"
              value={password}
              onChangeText={setPassword}
              editable={!loading}
            />
            <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
              {showPassword ? (
                <EyeOff color="#015551" size={20} style={styles.icon} />
              ) : (
                <Eye color="#015551" size={20} style={styles.icon} />
              )}
            </TouchableOpacity>
          </View>
        </View>

        <TouchableOpacity
          style={[styles.button, loading && styles.buttonDisabled]}
          onPress={handleLogin}
          disabled={loading}
        >
          <Text style={styles.buttonText}>
            {loading ? "Signing in..." : "Sign in"}
          </Text>
        </TouchableOpacity>

        <View style={styles.signupContainer}>
          <Text style={styles.signupText}>Don't have an account?</Text>
          <TouchableOpacity onPress={navigateToSignup} disabled={loading}>
            <Text style={styles.signupLink}>Sign up</Text>
          </TouchableOpacity>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    justifyContent: "center",
    marginLeft: 50,
  },
  title: {
    fontFamily: "SFProDisplay-Bold",
    fontSize: 30,
    fontWeight: "bold",
    color: "#015551",
  },
  subtitle: {
    fontFamily: "SFProDisplay-Bold",
    fontWeight: "bold",
    fontSize: 12,
    color: "#015551",
  },
  form: {
    marginTop: 40,
    width: "85%",
    shadowColor: "#000000",
    shadowOpacity: 0.3,
    shadowOffset: { width: 0, height: 4 },
    shadowRadius: 4,
    elevation: 4,
  },
  label: {
    fontFamily: "SFProDisplay-Bold",
    fontSize: 20,
    fontWeight: "bold",
    color: "#015551",
    marginBottom: 6,
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#015551",
    borderRadius: 20,
    paddingHorizontal: 10,
    marginBottom: 20,
  },
  icon: {
    marginHorizontal: 5,
  },
  input: {
    flex: 1,
    fontSize: 18,
    fontWeight: "bold",
    color: "#015551",
    paddingVertical: 10,
  },
  button: {
    width: "85%",
    height: 40,
    backgroundColor: "#015551",
    borderColor: "#015551",
    justifyContent: "center",
    alignItems: "center",
    marginTop: 20,
    borderRadius: 20,
    borderWidth: 1,
    shadowColor: "#000000",
    shadowOpacity: 0.3,
    shadowOffset: { width: 0, height: 4 },
    shadowRadius: 4,
    elevation: 4,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    fontFamily: "SFProDisplay-Bold",
    fontSize: 20,
    fontWeight: "bold",
    color: "#FFFFFF",
  },
  signupContainer: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: 20,
    width: "85%",
  },
  signupText: {
    color: "#015551",
    fontSize: 14,
    marginRight: 5,
  },
  signupLink: {
    color: "#015551",
    fontSize: 14,
    fontWeight: "bold",
    textDecorationLine: "underline",
  },
});
