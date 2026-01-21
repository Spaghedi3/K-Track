import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { getToken } from "./services/authStorage";

export default function RootLayout() {
  const [isAuth, setIsAuth] = useState<boolean | null>(null);

  useEffect(() => {
    (async () => {
      const token = await getToken();
      setIsAuth(!!token);
    })();
  }, []);

  if (isAuth === null) return null;

  return (
    <Stack screenOptions={{ headerShown: false }}>
      {isAuth ? <Stack.Screen name="Home" /> : <Stack.Screen name="index" />}
    </Stack>
  );
}
