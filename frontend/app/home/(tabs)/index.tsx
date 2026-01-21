import SimpleDropdown from "@/app/componets/SimpleDropdown";
import apiClient from "@/app/services/apiClient";
import { deleteToken } from "@/app/services/authStorage";
import { useFocusEffect } from "@react-navigation/native";
import * as ImagePicker from "expo-image-picker";
import { router } from "expo-router";
import {
  Activity,
  Award,
  BarChart3,
  Calendar,
  Camera,
  Clock,
  Dumbbell,
  Edit2,
  Flame,
  LogOut,
  Ruler,
  Target,
  TrendingUp,
  Trophy,
  User,
  Weight,
} from "lucide-react-native";
import React, { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Dimensions,
  Image,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

const { width } = Dimensions.get("window");

const CLOUDINARY_CLOUD_NAME =
  process.env.EXPO_PUBLIC_CLOUDINARY_CLOUD_NAME ?? "";
const CLOUDINARY_UPLOAD_PRESET =
  process.env.EXPO_PUBLIC_CLOUDINARY_UPLOAD_PRESET ?? "";

interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  profilePictureUrl?: string;
  age?: number;
  gender?: "MALE" | "FEMALE" | "OTHER";
  height?: number;
  weight?: number;
  goal?: "LOSE_WEIGHT" | "MAINTAIN_WEIGHT" | "GAIN_MUSCLE";
  activityLevel?: "LOW" | "MEDIUM" | "HIGH";
}

interface WorkoutStats {
  totalWorkouts: number;
  totalSets: number;
  totalReps: number;
  totalVolume: number;
  totalDuration: number;
  workoutsLast30Days: number;
  volumeLast30Days: number;
  averageWorkoutDuration: number;
  currentStreak: number;
  longestStreak: number;
  totalUniqueExercises: number;
  mostFrequentExercises: ExerciseFrequency[];
  muscleGroupDistribution: { [key: string]: MuscleGroupStats };
  workoutsByDayOfWeek: { [key: string]: number };
  personalRecords: PersonalRecord[];
}

interface ExerciseFrequency {
  exerciseId: number;
  exerciseName: string;
  imageUrl?: string;
  count: number;
  totalSets: number;
  totalVolume: number;
}

interface MuscleGroupStats {
  muscleName: string;
  workoutCount: number;
  totalSets: number;
  totalVolume: number;
}

interface PersonalRecord {
  exerciseId: number;
  exerciseName: string;
  weight: number;
  reps: number;
  estimatedOneRepMax: number;
  achievedAt: string;
}

const GENDER_OPTIONS = [
  { label: "Male", value: "MALE" },
  { label: "Female", value: "FEMALE" },
  { label: "Other", value: "OTHER" },
];

const GOAL_OPTIONS = [
  { label: "Lose Weight", value: "LOSE_WEIGHT" },
  { label: "Maintain Weight", value: "MAINTAIN_WEIGHT" },
  { label: "Gain Muscle", value: "GAIN_MUSCLE" },
];

const ACTIVITY_LEVEL_OPTIONS = [
  { label: "Low (1-2 days/week)", value: "LOW" },
  { label: "Medium (3-5 days/week)", value: "MEDIUM" },
  { label: "High (6-7 days/week)", value: "HIGH" },
];

export default function ProfileScreen() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [stats, setStats] = useState<WorkoutStats | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [saving, setSaving] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [editForm, setEditForm] = useState<Partial<UserProfile>>({});

  useEffect(() => {
    fetchData();
  }, []);

  useFocusEffect(
    React.useCallback(() => {
      fetchStats();
    }, []),
  );

  const fetchData = async () => {
    await Promise.all([fetchProfile(), fetchStats()]);
  };

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get("/api/users/profile");
      setProfile(response.data);
      setEditForm(response.data);
    } catch (error) {
      console.error("Error fetching profile:", error);
      Alert.alert("Error", "Could not load profile");
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      const response = await apiClient.get("/api/statistics/overview");
      setStats(response.data);
    } catch (error) {
      console.error("Error fetching statistics:", error);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchData();
    setRefreshing(false);
  };

  const pickImage = async () => {
    const permissionResult =
      await ImagePicker.requestMediaLibraryPermissionsAsync();

    if (!permissionResult.granted) {
      Alert.alert("Permission Required", "Please allow access to your photos");
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 0.8,
    });

    if (!result.canceled && result.assets[0]) {
      await uploadProfilePicture(result.assets[0].uri);
    }
  };

  const uploadProfilePicture = async (uri: string) => {
    try {
      setUploadingImage(true);

      const formData = new FormData();
      formData.append("file", {
        uri,
        type: "image/jpeg",
        name: "profile-picture.jpg",
      } as any);
      formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);

      const cloudinaryResponse = await fetch(
        `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
        {
          method: "POST",
          body: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      const cloudinaryData = await cloudinaryResponse.json();

      if (cloudinaryData.secure_url) {
        const response = await apiClient.put("/api/users/profile-picture", {
          profilePictureUrl: cloudinaryData.secure_url,
        });

        setProfile(response.data);
        setEditForm(response.data);
        Alert.alert("Success", "Profile picture updated!");
      } else {
        throw new Error("No URL returned from Cloudinary");
      }
    } catch (error) {
      console.error("Error uploading profile picture:", error);
      Alert.alert("Upload failed", "Could not upload profile picture");
    } finally {
      setUploadingImage(false);
    }
  };

  const handleSaveProfile = async () => {
    try {
      setSaving(true);

      const response = await apiClient.put("/api/users/profile", editForm);

      setProfile(response.data);
      setEditForm(response.data);
      setIsEditing(false);
      Alert.alert("Success", "Profile updated successfully!");
    } catch (error) {
      console.error("Error saving profile:", error);
      Alert.alert("Error", "Could not save profile");
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    Alert.alert(
      "Logout",
      "Are you sure you want to logout?",
      [
        {
          text: "Cancel",
          style: "cancel",
        },
        {
          text: "Logout",
          style: "destructive",
          onPress: async () => {
            try {
              await deleteToken();
              router.replace("/");
            } catch (error) {
              console.error("Error logging out:", error);
              Alert.alert("Error", "Could not logout");
            }
          },
        },
      ],
      { cancelable: true },
    );
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#3AAFA9" />
      </View>
    );
  }

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }
    >
      {/* Profile Header */}
      <View style={styles.header}>
        <View style={styles.profileImageContainer}>
          <TouchableOpacity onPress={pickImage} disabled={uploadingImage}>
            {uploadingImage ? (
              <View style={styles.profileImage}>
                <ActivityIndicator size="large" color="#3AAFA9" />
              </View>
            ) : profile?.profilePictureUrl ? (
              <Image
                source={{ uri: profile.profilePictureUrl }}
                style={styles.profileImage}
              />
            ) : (
              <View style={[styles.profileImage, styles.placeholderImage]}>
                <User size={48} color="#FEFFFF" strokeWidth={2} />
              </View>
            )}
            <View style={styles.editIconContainer}>
              <Camera size={16} color="#FEFFFF" strokeWidth={2.5} />
            </View>
          </TouchableOpacity>
        </View>

        <Text style={styles.name}>{profile?.fullName}</Text>
        <Text style={styles.email}>{profile?.email}</Text>

        {/* Quick Stats */}
        {stats && (
          <View style={styles.quickStatsContainer}>
            <View style={styles.quickStat}>
              <Text style={styles.quickStatValue}>{stats.totalWorkouts}</Text>
              <Text style={styles.quickStatLabel}>Workouts</Text>
            </View>
            <View style={styles.quickStatDivider} />
            <View style={styles.quickStat}>
              <Text style={styles.quickStatValue}>{stats.currentStreak}</Text>
              <Text style={styles.quickStatLabel}>Day Streak</Text>
            </View>
            <View style={styles.quickStatDivider} />
            <View style={styles.quickStat}>
              <Text style={styles.quickStatValue}>
                {Math.round(stats.totalVolume)}
              </Text>
              <Text style={styles.quickStatLabel}>Total kg</Text>
            </View>
          </View>
        )}
      </View>

      {/* Streak Cards */}
      {stats && (
        <View style={styles.streakContainer}>
          <View style={[styles.streakCard, styles.currentStreakCard]}>
            <Flame size={32} color="#FEFFFF" strokeWidth={2} />
            <Text style={styles.streakNumber}>{stats.currentStreak}</Text>
            <Text style={styles.streakLabel}>Current Streak</Text>
            <Text style={styles.streakSubtext}>days in a row</Text>
          </View>

          <View style={[styles.streakCard, styles.longestStreakCard]}>
            <Trophy size={32} color="#FEFFFF" strokeWidth={2} />
            <Text style={styles.streakNumber}>{stats.longestStreak}</Text>
            <Text style={styles.streakLabel}>Best Streak</Text>
            <Text style={styles.streakSubtext}>personal record</Text>
          </View>
        </View>
      )}

      {/* Personal Information */}
      <View style={styles.section}>
        <View style={styles.sectionHeader}>
          <View style={styles.sectionTitleContainer}>
            <User size={20} color="#17252A" strokeWidth={2} />
            <Text style={styles.sectionTitle}>Personal Information</Text>
          </View>
          <TouchableOpacity
            onPress={() => {
              if (isEditing) {
                setEditForm(profile || {});
              }
              setIsEditing(!isEditing);
            }}
            style={styles.editButton}
          >
            <Edit2 size={14} color="#3AAFA9" strokeWidth={2} />
            <Text style={styles.editButtonText}>
              {isEditing ? "Cancel" : "Edit"}
            </Text>
          </TouchableOpacity>
        </View>

        <View style={styles.infoGrid}>
          <InfoItem
            icon={<User size={18} color="#2B7A78" />}
            label="Full Name"
            value={editForm.fullName || "Not set"}
            isEditing={isEditing}
            onChangeText={(text) =>
              setEditForm({ ...editForm, fullName: text })
            }
          />
          <InfoItem
            icon={<Calendar size={18} color="#2B7A78" />}
            label="Age"
            value={editForm.age?.toString() || "Not set"}
            isEditing={isEditing}
            onChangeText={(text) =>
              setEditForm({ ...editForm, age: parseInt(text) || undefined })
            }
            keyboardType="numeric"
          />
          <View style={styles.infoItem}>
            <View style={styles.infoLabelContainer}>
              <Activity size={18} color="#2B7A78" />
              <Text style={styles.infoLabel}>Gender</Text>
            </View>
            {isEditing ? (
              <View style={{ flex: 1 }}>
                <SimpleDropdown
                  options={GENDER_OPTIONS}
                  value={editForm.gender}
                  onValueChange={(value) =>
                    setEditForm({ ...editForm, gender: value as any })
                  }
                  placeholder="Select gender"
                />
              </View>
            ) : (
              <Text style={styles.infoValue}>
                {formatGender(editForm.gender) || "Not set"}
              </Text>
            )}
          </View>
          <InfoItem
            icon={<Ruler size={18} color="#2B7A78" />}
            label="Height (cm)"
            value={editForm.height?.toString() || "Not set"}
            isEditing={isEditing}
            onChangeText={(text) =>
              setEditForm({
                ...editForm,
                height: parseFloat(text) || undefined,
              })
            }
            keyboardType="decimal-pad"
          />
          <InfoItem
            icon={<Weight size={18} color="#2B7A78" />}
            label="Weight (kg)"
            value={editForm.weight?.toString() || "Not set"}
            isEditing={isEditing}
            onChangeText={(text) =>
              setEditForm({
                ...editForm,
                weight: parseFloat(text) || undefined,
              })
            }
            keyboardType="decimal-pad"
          />
        </View>
      </View>

      {/* Fitness Goals */}
      <View style={styles.section}>
        <View style={styles.sectionTitleContainer}>
          <Target size={20} color="#17252A" strokeWidth={2} />
          <Text style={styles.sectionTitle}>Fitness Goals</Text>
        </View>
        <View style={styles.infoGrid}>
          <View style={styles.infoItem}>
            <View style={styles.infoLabelContainer}>
              <TrendingUp size={18} color="#2B7A78" />
              <Text style={styles.infoLabel}>Goal</Text>
            </View>
            {isEditing ? (
              <View style={{ flex: 1 }}>
                <SimpleDropdown
                  options={GOAL_OPTIONS}
                  value={editForm.goal}
                  onValueChange={(value) =>
                    setEditForm({ ...editForm, goal: value as any })
                  }
                  placeholder="Select goal"
                />
              </View>
            ) : (
              <Text style={styles.infoValue}>
                {formatGoal(editForm.goal) || "Not set"}
              </Text>
            )}
          </View>
          <View style={styles.infoItem}>
            <View style={styles.infoLabelContainer}>
              <Activity size={18} color="#2B7A78" />
              <Text style={styles.infoLabel}>Activity Level</Text>
            </View>
            {isEditing ? (
              <View style={{ flex: 1 }}>
                <SimpleDropdown
                  options={ACTIVITY_LEVEL_OPTIONS}
                  value={editForm.activityLevel}
                  onValueChange={(value) =>
                    setEditForm({ ...editForm, activityLevel: value as any })
                  }
                  placeholder="Select activity level"
                />
              </View>
            ) : (
              <Text style={styles.infoValue}>
                {formatActivityLevel(editForm.activityLevel) || "Not set"}
              </Text>
            )}
          </View>
        </View>
      </View>

      {isEditing && (
        <TouchableOpacity
          style={styles.saveButton}
          onPress={handleSaveProfile}
          disabled={saving}
        >
          {saving ? (
            <ActivityIndicator color="#FEFFFF" />
          ) : (
            <Text style={styles.saveButtonText}>Save Changes</Text>
          )}
        </TouchableOpacity>
      )}

      {/* Statistics Section */}
      {stats && (
        <>
          {/* Overall Stats Grid */}
          <View style={styles.section}>
            <View style={styles.sectionTitleContainer}>
              <BarChart3 size={20} color="#17252A" strokeWidth={2} />
              <Text style={styles.sectionTitle}>Overall Statistics</Text>
            </View>
            <View style={styles.statsGrid}>
              <StatCard
                icon={<Dumbbell size={24} color="#3AAFA9" strokeWidth={2} />}
                value={stats.totalWorkouts.toString()}
                label="Total Workouts"
              />
              <StatCard
                icon={<BarChart3 size={24} color="#3AAFA9" strokeWidth={2} />}
                value={stats.totalSets.toString()}
                label="Total Sets"
              />
              <StatCard
                icon={<Activity size={24} color="#3AAFA9" strokeWidth={2} />}
                value={stats.totalReps.toLocaleString()}
                label="Total Reps"
              />
              <StatCard
                icon={<Weight size={24} color="#3AAFA9" strokeWidth={2} />}
                value={`${Math.round(stats.totalVolume).toLocaleString()} kg`}
                label="Total Volume"
              />
              <StatCard
                icon={<Clock size={24} color="#3AAFA9" strokeWidth={2} />}
                value={`${Math.round(stats.totalDuration)} min`}
                label="Total Time"
              />
              <StatCard
                icon={<Target size={24} color="#3AAFA9" strokeWidth={2} />}
                value={stats.totalUniqueExercises.toString()}
                label="Unique Exercises"
              />
            </View>
          </View>

          {/* Recent Activity */}
          <View style={styles.section}>
            <View style={styles.sectionTitleContainer}>
              <TrendingUp size={20} color="#17252A" strokeWidth={2} />
              <Text style={styles.sectionTitle}>Last 30 Days</Text>
            </View>
            <View style={styles.recentActivityCard}>
              <View style={styles.recentActivityRow}>
                <View style={styles.recentActivityLabelContainer}>
                  <Dumbbell size={16} color="#2B7A78" strokeWidth={2} />
                  <Text style={styles.recentActivityLabel}>Workouts</Text>
                </View>
                <Text style={styles.recentActivityValue}>
                  {stats.workoutsLast30Days}
                </Text>
              </View>
              <View style={styles.recentActivityDivider} />
              <View style={styles.recentActivityRow}>
                <View style={styles.recentActivityLabelContainer}>
                  <Weight size={16} color="#2B7A78" strokeWidth={2} />
                  <Text style={styles.recentActivityLabel}>Volume</Text>
                </View>
                <Text style={styles.recentActivityValue}>
                  {Math.round(stats.volumeLast30Days).toLocaleString()} kg
                </Text>
              </View>
              <View style={styles.recentActivityDivider} />
              <View style={styles.recentActivityRow}>
                <View style={styles.recentActivityLabelContainer}>
                  <Clock size={16} color="#2B7A78" strokeWidth={2} />
                  <Text style={styles.recentActivityLabel}>Avg. Duration</Text>
                </View>
                <Text style={styles.recentActivityValue}>
                  {Math.round(stats.averageWorkoutDuration)} min
                </Text>
              </View>
            </View>
          </View>

          {/* Personal Records */}
          {stats.personalRecords && stats.personalRecords.length > 0 && (
            <View style={styles.section}>
              <View style={styles.sectionTitleContainer}>
                <Award size={20} color="#17252A" strokeWidth={2} />
                <Text style={styles.sectionTitle}>Personal Records</Text>
              </View>
              {stats.personalRecords.slice(0, 5).map((pr, index) => (
                <View key={pr.exerciseId} style={styles.prCard}>
                  <View style={styles.prHeader}>
                    <View style={styles.prRankBadge}>
                      <Text style={styles.prRankText}>#{index + 1}</Text>
                    </View>
                    <Text style={styles.prExerciseName}>{pr.exerciseName}</Text>
                  </View>
                  <View style={styles.prStats}>
                    <View style={styles.prStatItem}>
                      <Text style={styles.prStatLabel}>Weight</Text>
                      <Text style={styles.prStatValue}>{pr.weight} kg</Text>
                    </View>
                    <View style={styles.prStatDivider} />
                    <View style={styles.prStatItem}>
                      <Text style={styles.prStatLabel}>Reps</Text>
                      <Text style={styles.prStatValue}>{pr.reps}</Text>
                    </View>
                    <View style={styles.prStatDivider} />
                    <View style={styles.prStatItem}>
                      <Text style={styles.prStatLabel}>Est. 1RM</Text>
                      <Text style={styles.prStatValue}>
                        {Math.round(pr.estimatedOneRepMax)} kg
                      </Text>
                    </View>
                  </View>
                </View>
              ))}
            </View>
          )}

          {/* Most Frequent Exercises */}
          {stats.mostFrequentExercises &&
            stats.mostFrequentExercises.length > 0 && (
              <View style={styles.section}>
                <View style={styles.sectionTitleContainer}>
                  <Dumbbell size={20} color="#17252A" strokeWidth={2} />
                  <Text style={styles.sectionTitle}>
                    Most Frequent Exercises
                  </Text>
                </View>
                {stats.mostFrequentExercises
                  .slice(0, 5)
                  .map((exercise, index) => (
                    <View
                      key={exercise.exerciseId}
                      style={styles.exerciseFreqCard}
                    >
                      <View style={styles.exerciseFreqHeader}>
                        <View style={styles.exerciseFreqRankBadge}>
                          <Text style={styles.exerciseFreqRankText}>
                            {index + 1}
                          </Text>
                        </View>
                        <Text style={styles.exerciseFreqName}>
                          {exercise.exerciseName}
                        </Text>
                      </View>
                      <View style={styles.exerciseFreqStats}>
                        <View style={styles.exerciseFreqStatItem}>
                          <Activity size={14} color="#2B7A78" strokeWidth={2} />
                          <Text style={styles.exerciseFreqStat}>
                            {exercise.count}x
                          </Text>
                        </View>
                        <View style={styles.exerciseFreqStatItem}>
                          <BarChart3
                            size={14}
                            color="#2B7A78"
                            strokeWidth={2}
                          />
                          <Text style={styles.exerciseFreqStat}>
                            {exercise.totalSets} sets
                          </Text>
                        </View>
                        <View style={styles.exerciseFreqStatItem}>
                          <Weight size={14} color="#2B7A78" strokeWidth={2} />
                          <Text style={styles.exerciseFreqStat}>
                            {Math.round(exercise.totalVolume).toLocaleString()}{" "}
                            kg
                          </Text>
                        </View>
                      </View>
                    </View>
                  ))}
              </View>
            )}

          {/* Workouts by Day */}
          <View style={styles.section}>
            <View style={styles.sectionTitleContainer}>
              <Calendar size={20} color="#17252A" strokeWidth={2} />
              <Text style={styles.sectionTitle}>Weekly Activity</Text>
            </View>
            <View style={styles.dayOfWeekContainer}>
              {Object.entries(stats.workoutsByDayOfWeek).map(([day, count]) => (
                <DayBar
                  key={day}
                  day={day.substring(0, 3)}
                  count={count}
                  maxCount={Math.max(
                    ...Object.values(stats.workoutsByDayOfWeek),
                  )}
                />
              ))}
            </View>
          </View>

          {/* Muscle Group Distribution */}
          {stats.muscleGroupDistribution &&
            Object.keys(stats.muscleGroupDistribution).length > 0 && (
              <View style={styles.section}>
                <View style={styles.sectionTitleContainer}>
                  <Target size={20} color="#17252A" strokeWidth={2} />
                  <Text style={styles.sectionTitle}>Muscle Group Focus</Text>
                </View>
                {Object.entries(stats.muscleGroupDistribution)
                  .sort((a, b) => b[1].totalVolume - a[1].totalVolume)
                  .slice(0, 8)
                  .map(([muscle, data]) => (
                    <View key={muscle} style={styles.muscleGroupCard}>
                      <View style={styles.muscleGroupHeader}>
                        <Text style={styles.muscleGroupName}>
                          {muscle.charAt(0).toUpperCase() + muscle.slice(1)}
                        </Text>
                      </View>
                      <View style={styles.muscleGroupStats}>
                        <View style={styles.muscleGroupStatItem}>
                          <Dumbbell size={12} color="#2B7A78" strokeWidth={2} />
                          <Text style={styles.muscleGroupStat}>
                            {data.workoutCount} workouts
                          </Text>
                        </View>
                        <View style={styles.muscleGroupStatItem}>
                          <BarChart3
                            size={12}
                            color="#2B7A78"
                            strokeWidth={2}
                          />
                          <Text style={styles.muscleGroupStat}>
                            {data.totalSets} sets
                          </Text>
                        </View>
                        <View style={styles.muscleGroupStatItem}>
                          <Weight size={12} color="#2B7A78" strokeWidth={2} />
                          <Text style={styles.muscleGroupStat}>
                            {Math.round(data.totalVolume).toLocaleString()} kg
                          </Text>
                        </View>
                      </View>
                    </View>
                  ))}
              </View>
            )}
        </>
      )}

      {/* Logout Button */}
      <View style={styles.logoutContainer}>
        <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
          <LogOut size={20} color="#DC2626" strokeWidth={2} />
          <Text style={styles.logoutButtonText}>Logout</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.bottomSpacing} />
    </ScrollView>
  );
}

// Info Item Component
interface InfoItemProps {
  icon: React.ReactNode;
  label: string;
  value: string;
  isEditing: boolean;
  onChangeText?: (text: string) => void;
  keyboardType?: "default" | "numeric" | "decimal-pad";
}

const InfoItem: React.FC<InfoItemProps> = ({
  icon,
  label,
  value,
  isEditing,
  onChangeText,
  keyboardType = "default",
}) => (
  <View style={styles.infoItem}>
    <View style={styles.infoLabelContainer}>
      {icon}
      <Text style={styles.infoLabel}>{label}</Text>
    </View>
    {isEditing && onChangeText ? (
      <TextInput
        style={styles.infoInput}
        value={value === "Not set" ? "" : value}
        onChangeText={onChangeText}
        keyboardType={keyboardType}
        placeholderTextColor="#2B7A78"
      />
    ) : (
      <Text style={styles.infoValue}>{value}</Text>
    )}
  </View>
);

// Stat Card Component
const StatCard: React.FC<{
  icon: React.ReactNode;
  value: string;
  label: string;
}> = ({ icon, value, label }) => (
  <View style={styles.statCard}>
    <View style={styles.statIconContainer}>{icon}</View>
    <Text style={styles.statValue}>{value}</Text>
    <Text style={styles.statLabel}>{label}</Text>
  </View>
);

// Day Bar Component
const DayBar: React.FC<{
  day: string;
  count: number;
  maxCount: number;
}> = ({ day, count, maxCount }) => {
  const height = maxCount > 0 ? (count / maxCount) * 100 : 0;

  return (
    <View style={styles.dayBarContainer}>
      <View style={styles.dayBarWrapper}>
        <View style={[styles.dayBar, { height: `${height}%` }]}>
          {count > 0 && <Text style={styles.dayBarCount}>{count}</Text>}
        </View>
      </View>
      <Text style={styles.dayBarLabel}>{day}</Text>
    </View>
  );
};

const formatGender = (gender?: string) => {
  if (!gender) return undefined;
  return gender.charAt(0) + gender.slice(1).toLowerCase();
};

const formatGoal = (goal?: string) => {
  if (!goal) return undefined;
  return goal
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (l) => l.toUpperCase());
};

const formatActivityLevel = (level?: string) => {
  if (!level) return undefined;
  return level
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (l) => l.toUpperCase());
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FEFFFF",
  },
  centerContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#FEFFFF",
  },
  header: {
    backgroundColor: "#DEF2F1",
    paddingTop: 60,
    paddingBottom: 30,
    alignItems: "center",
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
  },
  profileImageContainer: {
    position: "relative",
    marginBottom: 16,
  },
  profileImage: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: "#3AAFA9",
    borderWidth: 4,
    borderColor: "#FEFFFF",
    justifyContent: "center",
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 8,
  },
  placeholderImage: {
    backgroundColor: "#3AAFA9",
  },
  editIconContainer: {
    position: "absolute",
    bottom: 0,
    right: 0,
    backgroundColor: "#3AAFA9",
    borderRadius: 20,
    width: 40,
    height: 40,
    justifyContent: "center",
    alignItems: "center",
    borderWidth: 3,
    borderColor: "#FEFFFF",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 4,
  },
  name: {
    fontSize: 26,
    fontWeight: "bold",
    color: "#17252A",
    marginBottom: 4,
  },
  email: {
    fontSize: 14,
    color: "#2B7A78",
    marginBottom: 20,
  },
  quickStatsContainer: {
    flexDirection: "row",
    backgroundColor: "#FEFFFF",
    borderRadius: 16,
    padding: 16,
    marginHorizontal: 20,
    marginTop: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  quickStat: {
    flex: 1,
    alignItems: "center",
  },
  quickStatValue: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#3AAFA9",
    marginBottom: 4,
  },
  quickStatLabel: {
    fontSize: 12,
    color: "#2B7A78",
    fontWeight: "500",
  },
  quickStatDivider: {
    width: 1,
    backgroundColor: "#DEF2F1",
    marginHorizontal: 12,
  },

  // Streak Cards
  streakContainer: {
    flexDirection: "row",
    padding: 20,
    gap: 12,
  },
  streakCard: {
    flex: 1,
    padding: 20,
    borderRadius: 16,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 6,
  },
  currentStreakCard: {
    backgroundColor: "#3AAFA9",
  },
  longestStreakCard: {
    backgroundColor: "#2B7A78",
  },
  streakNumber: {
    fontSize: 40,
    fontWeight: "bold",
    color: "#FEFFFF",
    marginTop: 12,
    marginBottom: 8,
  },
  streakLabel: {
    fontSize: 15,
    fontWeight: "600",
    color: "#FEFFFF",
  },
  streakSubtext: {
    fontSize: 12,
    color: "#DEF2F1",
    marginTop: 4,
  },

  // Sections
  section: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: "#DEF2F1",
  },
  sectionHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  sectionTitleContainer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: "700",
    color: "#17252A",
  },
  editButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 10,
    borderWidth: 1.5,
    borderColor: "#3AAFA9",
    backgroundColor: "#FEFFFF",
  },
  editButtonText: {
    color: "#3AAFA9",
    fontWeight: "600",
    fontSize: 14,
  },

  // Info Grid
  infoGrid: {
    gap: 12,
  },
  infoItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 16,
    paddingHorizontal: 16,
    backgroundColor: "#DEF2F1",
    borderRadius: 12,
  },
  infoLabelContainer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    flex: 1,
  },
  infoLabel: {
    fontSize: 14,
    color: "#2B7A78",
    fontWeight: "600",
  },
  infoValue: {
    fontSize: 14,
    color: "#17252A",
    fontWeight: "600",
    flex: 1,
    textAlign: "right",
  },
  infoInput: {
    fontSize: 14,
    color: "#17252A",
    fontWeight: "500",
    flex: 1,
    textAlign: "right",
    backgroundColor: "#FEFFFF",
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: "#3AAFA9",
  },
  saveButton: {
    backgroundColor: "#3AAFA9",
    margin: 20,
    padding: 18,
    borderRadius: 12,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 8,
    elevation: 6,
  },
  saveButtonText: {
    color: "#FEFFFF",
    fontSize: 16,
    fontWeight: "700",
  },

  // Stats Grid
  statsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 12,
  },
  statCard: {
    width: (width - 52) / 2,
    backgroundColor: "#FEFFFF",
    padding: 16,
    borderRadius: 12,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#DEF2F1",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 3,
  },
  statIconContainer: {
    marginBottom: 12,
  },
  statValue: {
    fontSize: 20,
    fontWeight: "bold",
    color: "#17252A",
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: "#2B7A78",
    textAlign: "center",
    fontWeight: "500",
  },

  // Recent Activity
  recentActivityCard: {
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: "#DEF2F1",
  },
  recentActivityRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 12,
  },
  recentActivityLabelContainer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  recentActivityLabel: {
    fontSize: 14,
    color: "#2B7A78",
    fontWeight: "600",
  },
  recentActivityValue: {
    fontSize: 16,
    color: "#17252A",
    fontWeight: "700",
  },
  recentActivityDivider: {
    height: 1,
    backgroundColor: "#DEF2F1",
  },

  // Personal Records
  prCard: {
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#DEF2F1",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 3,
  },
  prHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 16,
  },
  prRankBadge: {
    backgroundColor: "#3AAFA9",
    width: 36,
    height: 36,
    borderRadius: 18,
    justifyContent: "center",
    alignItems: "center",
    marginRight: 12,
  },
  prRankText: {
    fontSize: 14,
    fontWeight: "bold",
    color: "#FEFFFF",
  },
  prExerciseName: {
    fontSize: 16,
    fontWeight: "600",
    color: "#17252A",
    flex: 1,
  },
  prStats: {
    flexDirection: "row",
    justifyContent: "space-around",
    backgroundColor: "#DEF2F1",
    borderRadius: 8,
    padding: 12,
  },
  prStatItem: {
    alignItems: "center",
  },
  prStatLabel: {
    fontSize: 12,
    color: "#2B7A78",
    marginBottom: 6,
    fontWeight: "600",
  },
  prStatValue: {
    fontSize: 16,
    fontWeight: "bold",
    color: "#17252A",
  },
  prStatDivider: {
    width: 1,
    backgroundColor: "#FEFFFF",
  },

  // Exercise Frequency
  exerciseFreqCard: {
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#DEF2F1",
  },
  exerciseFreqHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  exerciseFreqRankBadge: {
    backgroundColor: "#DEF2F1",
    width: 32,
    height: 32,
    borderRadius: 16,
    justifyContent: "center",
    alignItems: "center",
    marginRight: 12,
  },
  exerciseFreqRankText: {
    fontSize: 14,
    fontWeight: "bold",
    color: "#3AAFA9",
  },
  exerciseFreqName: {
    fontSize: 15,
    fontWeight: "600",
    color: "#17252A",
    flex: 1,
  },
  exerciseFreqStats: {
    flexDirection: "row",
    justifyContent: "space-around",
    backgroundColor: "#DEF2F1",
    borderRadius: 8,
    padding: 10,
  },
  exerciseFreqStatItem: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
  },
  exerciseFreqStat: {
    fontSize: 12,
    color: "#2B7A78",
    fontWeight: "600",
  },

  // Day of Week
  dayOfWeekContainer: {
    flexDirection: "row",
    justifyContent: "space-around",
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    padding: 16,
    height: 150,
    borderWidth: 1,
    borderColor: "#DEF2F1",
  },
  dayBarContainer: {
    flex: 1,
    alignItems: "center",
  },
  dayBarWrapper: {
    flex: 1,
    width: "100%",
    justifyContent: "flex-end",
    alignItems: "center",
  },
  dayBar: {
    width: 32,
    backgroundColor: "#3AAFA9",
    borderRadius: 6,
    justifyContent: "flex-start",
    alignItems: "center",
    paddingTop: 6,
    minHeight: 24,
  },
  dayBarCount: {
    fontSize: 11,
    fontWeight: "bold",
    color: "#FEFFFF",
  },
  dayBarLabel: {
    fontSize: 12,
    color: "#2B7A78",
    fontWeight: "600",
    marginTop: 8,
  },

  // Muscle Groups
  muscleGroupCard: {
    backgroundColor: "#FEFFFF",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#DEF2F1",
  },
  muscleGroupHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  muscleGroupName: {
    fontSize: 16,
    fontWeight: "600",
    color: "#17252A",
  },
  muscleGroupStats: {
    flexDirection: "row",
    justifyContent: "space-around",
    backgroundColor: "#DEF2F1",
    borderRadius: 8,
    padding: 10,
  },
  muscleGroupStatItem: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
  },
  muscleGroupStat: {
    fontSize: 12,
    color: "#2B7A78",
    fontWeight: "600",
  },

  // Logout
  logoutContainer: {
    padding: 20,
    paddingTop: 30,
  },
  logoutButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 10,
    backgroundColor: "#FEFFFF",
    paddingVertical: 16,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: "#DC2626",
  },
  logoutButtonText: {
    fontSize: 16,
    fontWeight: "600",
    color: "#DC2626",
  },

  bottomSpacing: {
    height: 40,
  },
});
