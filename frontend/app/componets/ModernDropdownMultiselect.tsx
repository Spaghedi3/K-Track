import { ChevronDown } from "lucide-react-native";
import React, { useState } from "react";
import {
  Modal,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

interface DropdownOption {
  label: string;
  value: string;
}

interface ModernDropdownProps {
  options: DropdownOption[];
  value?: string | string[];
  onValueChange?: (value: string) => void;
  onValuesChange?: (values: string[]) => void;
  placeholder?: string;
  multiSelect?: boolean;
}

export default function ModernDropdownMultiselect({
  options,
  value,
  onValueChange,
  onValuesChange,
  placeholder = "Select an option",
  multiSelect = false,
}: ModernDropdownProps) {
  const [isOpen, setIsOpen] = useState(false);

  // Handle both single and multi-select
  const selectedValues = multiSelect
    ? (value as string[]) || []
    : value
      ? [value as string]
      : [];

  const getDisplayText = () => {
    if (selectedValues.length === 0) return placeholder;

    if (multiSelect) {
      if (selectedValues.length === 1) {
        const option = options.find((opt) => opt.value === selectedValues[0]);
        return option ? option.label : placeholder;
      }
      return `${selectedValues.length} selected`;
    }

    const option = options.find((opt) => opt.value === selectedValues[0]);
    return option ? option.label : placeholder;
  };

  const handleSelect = (optionValue: string) => {
    if (multiSelect) {
      const newValues = selectedValues.includes(optionValue)
        ? selectedValues.filter((v) => v !== optionValue)
        : [...selectedValues, optionValue];

      onValuesChange?.(newValues);
    } else {
      onValueChange?.(optionValue);
      setIsOpen(false);
    }
  };

  const handleDone = () => {
    setIsOpen(false);
  };

  return (
    <>
      <TouchableOpacity
        style={styles.dropdown}
        onPress={() => setIsOpen(true)}
        activeOpacity={0.7}
      >
        <Text
          style={[
            styles.dropdownText,
            selectedValues.length === 0 && styles.placeholderText,
          ]}
        >
          {getDisplayText()}
        </Text>
        <ChevronDown color="#015551" size={20} />
      </TouchableOpacity>

      <Modal
        visible={isOpen}
        transparent
        animationType="fade"
        onRequestClose={() => setIsOpen(false)}
      >
        <TouchableOpacity
          style={styles.modalOverlay}
          activeOpacity={1}
          onPress={() => setIsOpen(false)}
        >
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>
                {multiSelect ? "Select options" : "Select an option"}
              </Text>
              <TouchableOpacity onPress={handleDone} style={styles.closeButton}>
                {multiSelect ? (
                  <Text style={styles.doneButtonText}>Done</Text>
                ) : (
                  <Text style={styles.closeButtonText}>✕</Text>
                )}
              </TouchableOpacity>
            </View>

            <ScrollView style={styles.optionsList}>
              {options.map((option) => {
                const isSelected = selectedValues.includes(option.value);

                return (
                  <TouchableOpacity
                    key={option.value}
                    style={[styles.option, isSelected && styles.selectedOption]}
                    onPress={() => handleSelect(option.value)}
                  >
                    <Text
                      style={[
                        styles.optionText,
                        isSelected && styles.selectedOptionText,
                      ]}
                    >
                      {option.label}
                    </Text>
                    {isSelected && (
                      <View style={styles.checkmark}>
                        <Text style={styles.checkmarkText}>✓</Text>
                      </View>
                    )}
                  </TouchableOpacity>
                );
              })}
            </ScrollView>
          </View>
        </TouchableOpacity>
      </Modal>

      {/* Show selected tags for multi-select */}
      {multiSelect && selectedValues.length > 0 && (
        <View style={styles.tagsContainer}>
          {selectedValues.map((val) => {
            const option = options.find((opt) => opt.value === val);
            if (!option) return null;

            return (
              <View key={val} style={styles.tag}>
                <Text style={styles.tagText}>{option.label}</Text>
                <TouchableOpacity onPress={() => handleSelect(val)}>
                  <Text style={styles.removeTag}>✕</Text>
                </TouchableOpacity>
              </View>
            );
          })}
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  dropdown: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderWidth: 1.5,
    borderColor: "#e0e0e0",
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 14,
    backgroundColor: "#fff",
    minHeight: 48,
  },
  dropdownText: {
    fontSize: 16,
    color: "#015551",
    fontWeight: "500",
    flex: 1,
  },
  placeholderText: {
    color: "#a0a0a0",
    fontWeight: "400",
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  modalContent: {
    backgroundColor: "#fff",
    borderRadius: 20,
    width: "100%",
    maxWidth: 400,
    maxHeight: "70%",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.3,
    shadowRadius: 20,
    elevation: 10,
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: "#f0f0f0",
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "bold",
    color: "#015551",
  },
  closeButton: {
    minWidth: 60,
    height: 32,
    borderRadius: 16,
    backgroundColor: "#015551",
    justifyContent: "center",
    alignItems: "center",
    paddingHorizontal: 12,
  },
  closeButtonText: {
    fontSize: 18,
    color: "#fff",
    fontWeight: "bold",
  },
  doneButtonText: {
    fontSize: 16,
    color: "#fff",
    fontWeight: "600",
  },
  optionsList: {
    maxHeight: 400,
  },
  option: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 16,
    paddingHorizontal: 20,
    borderBottomWidth: 1,
    borderBottomColor: "#f5f5f5",
  },
  selectedOption: {
    backgroundColor: "#f0f9f8",
  },
  optionText: {
    fontSize: 16,
    color: "#333",
    flex: 1,
    textTransform: "capitalize",
  },
  selectedOptionText: {
    color: "#015551",
    fontWeight: "600",
  },
  checkmark: {
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: "#015551",
    justifyContent: "center",
    alignItems: "center",
  },
  checkmarkText: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "bold",
  },
  tagsContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    marginTop: 12,
    gap: 8,
  },
  tag: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#3AAFA9",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    gap: 6,
  },
  tagText: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "500",
    textTransform: "capitalize",
  },
  removeTag: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "bold",
    marginLeft: 4,
  },
});
