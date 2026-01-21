import React, { useEffect, useRef } from "react";
import { Animated, Dimensions } from "react-native";
import Svg, { Path } from "react-native-svg";

const { width } = Dimensions.get("window");
const color = "#57B4BA";

export default function Wave() {
  const slideAnim = useRef(new Animated.Value(300)).current;

  useEffect(() => {
    Animated.spring(slideAnim, {
      toValue: 0,
      tension: 40,
      friction: 7,
      delay: 1000,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View
      style={{
        position: "absolute",
        bottom: 0,
        transform: [{ translateY: slideAnim }],
      }}
    >
      <Svg
        width={width}
        height={900}
        viewBox={"0 0 430 932"}
        preserveAspectRatio="none"
      >
        <Path
          d="M0 618L18 636.2C36 654.3 72 690.7 107.8 689.3C143.7 688 179.3 649 215.2 656.8C251 664.7 287 719.3 322.8 708C358.7 696.7 394.3 619.3 412.2 580.7L430 542L430 933L412.2 933C394.3 933 358.7 933 322.8 933C287 933 251 933 215.2 933C179.3 933 143.7 933 107.8 933C72 933 36 933 18 933L0 933Z"
          fill={color}
        />
      </Svg>
    </Animated.View>
  );
}
