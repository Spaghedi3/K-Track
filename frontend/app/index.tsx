import { router } from "expo-router";
import { useEffect, useRef } from "react";
import {
  Animated,
  Dimensions,
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import Wave from "./componets/Wave";

const { width, height } = Dimensions.get("window");

export default function Index() {
  const slideAnim = useRef(new Animated.Value(-300)).current;

  useEffect(() => {
    Animated.spring(slideAnim, {
      toValue: 0,
      tension: 40,
      friction: 7,
      useNativeDriver: true,
    }).start();
  }, []);

  const navigateToSignUp = () => {
    router.replace("/auth/Signup");
  };
  const navigateToLogin = () => {
    router.replace("/auth/Login");
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <Wave />
      <View style={styles.container}>
        <Animated.View
          style={[
            styles.imageContainer,
            {
              transform: [{ translateY: slideAnim }],
            },
          ]}
        >
          <Animated.Image
            source={require("../assets/images/(3D) - gym.png")}
            style={styles.image}
            resizeMode="contain"
          />
        </Animated.View>
        <Text style={styles.welcomeText}>Welcome</Text>
        <View style={styles.spacer} />
        <View style={styles.buttonContainer}>
          <TouchableOpacity style={styles.button} onPress={navigateToSignUp}>
            <Text style={styles.buttonText}>Sign Up</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.loginButton}
            onPress={navigateToLogin}
          >
            <Text style={styles.loginText}>Log in</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },
  container: {
    flex: 1,
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 20,
  },
  imageContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    maxHeight: height * 0.4,
  },
  image: {
    width: width * 0.85,
    height: "100%",
  },
  welcomeText: {
    color: "#015551",
    fontFamily: "Montserrat-Bold",
    fontSize: 40,
    marginTop: 10,
  },
  spacer: {
    flex: 1,
  },
  buttonContainer: {
    justifyContent: "center",
    alignItems: "center",
    paddingBottom: 20,
    zIndex: 1,
  },
  button: {
    width: 248,
    height: 41,
    backgroundColor: "#FFFFFF",
    borderRadius: 30,
    justifyContent: "center",
    alignItems: "center",
    shadowColor: "#000",
    shadowOpacity: 0.25,
    shadowOffset: {
      width: 0,
      height: 4,
    },
    marginBottom: 20,
  },
  buttonText: {
    color: "#015551",
    fontSize: 20,
    fontFamily: "SFProDisplay-Bold",
  },
  loginButton: {
    width: 248,
    height: 41,
    backgroundColor: "#3D9BA1",
    borderRadius: 30,
    justifyContent: "center",
    alignItems: "center",
    shadowColor: "#000",
    shadowOpacity: 0.25,
    shadowOffset: {
      width: 0,
      height: 4,
    },
    marginTop: 20,
  },
  loginText: {
    color: "#FFFFFF",
    fontSize: 20,
    fontFamily: "SFProDisplay-Bold",
  },
});
