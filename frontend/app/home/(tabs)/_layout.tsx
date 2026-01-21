import { FontAwesome } from "@expo/vector-icons";
import { Tabs } from "expo-router";

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: "#3AAFA9",
        headerShown: false,
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Profile",
          tabBarIcon: ({ color }) => (
            <FontAwesome size={28} name="user" color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="exercises"
        options={{
          title: "Exercises",
          tabBarIcon: ({ color }) => (
            <FontAwesome size={28} name="list-alt" color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="workout_template"
        options={{
          title: "Workout Templates",
          tabBarIcon: ({ color }) => (
            <FontAwesome size={28} name="cog" color={color} />
          ),
        }}
      />

      <Tabs.Screen
        name="start_workout"
        options={{
          title: "Start Workout",
          tabBarIcon: ({ color }) => (
            <FontAwesome size={28} name="play-circle" color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
