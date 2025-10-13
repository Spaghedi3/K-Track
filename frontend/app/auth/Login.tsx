import { Eye, EyeOff, Lock, Mail } from "lucide-react-native";
import React, { useState } from "react";
import {
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

export default function Login() {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Sign in</Text>
      <Text style={styles.subtitle}>
        To proced with your account you must sign in.
      </Text>

      <View style={styles.form}>
        {/* Email */}
        <Text style={styles.label}>Email</Text>
        <View style={styles.inputContainer}>
          <Mail color="#015551" size={20} style={styles.icon} />
          <TextInput
            style={styles.input}
            placeholder="Enter your email"
            keyboardType="email-address"
            placeholderTextColor="rgba(0,0,0,0.2)"
          />
        </View>

        {/* Password */}
        <Text style={styles.label}>Password</Text>
        <View style={styles.inputContainer}>
          <Lock color="#015551" size={20} style={styles.icon} />
          <TextInput
            style={styles.input}
            placeholder="Enter your password"
            secureTextEntry={!showPassword}
            placeholderTextColor="rgba(0,0,0,0.2)"
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
        style={{
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
        }}
      >
        <Text
          style={{
            fontFamily: "SFProDisplay-Bold",
            fontSize: 20,
            fontWeight: "bold",
            color: "#FFFFFF",
            marginBottom: 6,
          }}
        >
          Sign in
        </Text>
      </TouchableOpacity>
    </View>
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
});
