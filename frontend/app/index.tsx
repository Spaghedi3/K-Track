import { router } from "expo-router";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import Wave from "./componets/Wave";

export default function Index() {
  const navigateToSignUp = () => {
    router.push("/auth/Signup");
  };
  const navigateToLogin = () => {
    router.push("/auth/Login");
  };

  return (
    <View
      style={{
        flex: 1,
        justifyContent: "flex-start",
        alignItems: "center",
        paddingTop: 50,
        backgroundColor: "#FFFFFF",
      }}
    >
      <Image
        source={require("../assets/images/(3D) - gym.png")}
        style={{
          width: 430,
          height: 446,
        }}
      />
      <Text
        style={{
          color: "#015551",
          fontFamily: "Montserrat-Bold",
          fontSize: 40,
        }}
      >
        Welcome
      </Text>
      <Wave />
      <TouchableOpacity style={styles.button} onPress={navigateToSignUp}>
        <Text
          style={{
            color: "#015551",
            fontSize: 20,
            fontFamily: "SFProDisplay-Bold",
          }}
        >
          Sign Up
        </Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={navigateToLogin}>
        <Text
          style={{
            marginTop: 40,
            color: "#FFFFFF",
            fontSize: 20,
            fontFamily: "SFProDisplay-Bold",
          }}
        >
          Log in
        </Text>
      </TouchableOpacity>
    </View>
  );
}
const styles = StyleSheet.create({
  button: {
    marginTop: 200,
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
  },
});
