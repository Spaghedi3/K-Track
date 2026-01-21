import React, { useState } from "react";
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Modal,
  ScrollView,
} from "react-native";

interface DropdownOption {
  label: string;
  value: string;
}

interface SimpleDropdownProps {
  options: DropdownOption[];
  value?: string;
  onValueChange: (value: string) => void;
  placeholder?: string;
}

export default function SimpleDropdown({
  options,
  value,
  onValueChange,
  placeholder = "Select an option",
}: SimpleDropdownProps) {
  const [visible, setVisible] = useState(false);

  const selectedOption = options.find((opt) => opt.value === value);

  return (
    <>
      <TouchableOpacity
        style={styles.dropdownButton}
        onPress={() => setVisible(true)}
      >
        <Text
          style={[
            styles.dropdownButtonText,
            !selectedOption && styles.placeholder,
          ]}
        >
          {selectedOption?.label || placeholder}
        </Text>
        <Text style={styles.arrow}>▼</Text>
      </TouchableOpacity>

      <Modal visible={visible} transparent animationType="fade">
        <TouchableOpacity
          style={styles.modalOverlay}
          activeOpacity={1}
          onPress={() => setVisible(false)}
        >
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Select {placeholder}</Text>
              <TouchableOpacity onPress={() => setVisible(false)}>
                <Text style={styles.closeButton}>✕</Text>
              </TouchableOpacity>
            </View>

            <ScrollView style={styles.optionsList}>
              {options.map((option) => (
                <TouchableOpacity
                  key={option.value}
                  style={[
                    styles.optionItem,
                    option.value === value && styles.selectedOption,
                  ]}
                  onPress={() => {
                    onValueChange(option.value);
                    setVisible(false);
                  }}
                >
                  <Text
                    style={[
                      styles.optionText,
                      option.value === value && styles.selectedOptionText,
                    ]}
                  >
                    {option.label}
                  </Text>
                  {option.value === value && (
                    <Text style={styles.checkmark}>✓</Text>
                  )}
                </TouchableOpacity>
              ))}
            </ScrollView>
          </View>
        </TouchableOpacity>
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  dropdownButton: {
    backgroundColor: "#FEFFFF",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: "#3AAFA9",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  dropdownButtonText: {
    fontSize: 14,
    color: "#17252A",
    fontWeight: "500",
  },
  placeholder: {
    color: "#2B7A78",
    opacity: 0.7,
  },
  arrow: {
    fontSize: 10,
    color: "#2B7A78",
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    justifyContent: "center",
    padding: 20,
  },
  modalContent: {
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    maxHeight: "70%",
    overflow: "hidden",
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: "#DEF2F1",
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "600",
    color: "#17252A",
  },
  closeButton: {
    fontSize: 24,
    color: "#2B7A78",
    fontWeight: "300",
  },
  optionsList: {
    maxHeight: 400,
  },
  optionItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: "#DEF2F1",
  },
  selectedOption: {
    backgroundColor: "#DEF2F1",
  },
  optionText: {
    fontSize: 16,
    color: "#17252A",
  },
  selectedOptionText: {
    color: "#3AAFA9",
    fontWeight: "600",
  },
  checkmark: {
    fontSize: 20,
    color: "#3AAFA9",
    fontWeight: "bold",
  },
});