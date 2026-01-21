package com.danis.backend.seeders;

import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.domain.model.enums.ExerciseType;
import com.danis.backend.domain.repository.ExerciseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseSeeder implements CommandLineRunner {

    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_BASE_URL = "https://www.ascendapi.com/api/v1/exercises";
    private static final int LIMIT = 100; // Increased to 100 for faster seeding
    private static final int MAX_PAGES = 200; // Safety limit

    @Override
    public void run(String... args) {
        log.info("Starting Exercise Seeder...");


        long existingCount = exerciseRepository.count();
        if (existingCount > 0) {
            log.info("Found {} exercises in database. Skipping seeding.", existingCount);
            return;
        }

        seedExercises();

        log.info("Exercise seeding completed!");
    }

    private void seedExercises() {
        int offset = 0;
        int totalSeeded = 0;
        int emptyResponseCount = 0;

        for (int page = 0; page < MAX_PAGES; page++) {
            try {
                String url = String.format("%s?offset=%d&limit=%d&sortBy=targetMuscles&sortOrder=desc",
                        API_BASE_URL, offset, LIMIT);

                log.info("Fetching page {} (offset: {})", page + 1, offset);

                String response = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(response);

                // Check success field
                JsonNode successNode = root.get("success");
                if (successNode == null || !successNode.asBoolean()) {
                    log.error("API returned success=false or null");
                    break;
                }

                // Get data array
                JsonNode data = root.get("data");
                if (data == null || !data.isArray()) {
                    log.error("Data field is null or not an array");
                    break;
                }

                // Check if data is empty
                if (data.isEmpty()) {
                    emptyResponseCount++;
                    log.warn("Empty data array received (count: {})", emptyResponseCount);

                    // If we get 3 empty responses in a row, stop
                    if (emptyResponseCount >= 3) {
                        log.info("Received 3 consecutive empty responses. Stopping.");
                        break;
                    }

                    offset += LIMIT;
                    continue;
                }

                // Reset empty response counter
                emptyResponseCount = 0;

                // Map and save exercises
                List<Exercise> exercises = new ArrayList<>();
                for (JsonNode exerciseNode : data) {
                    try {
                        Exercise exercise = mapToExercise(exerciseNode);
                        exercises.add(exercise);
                    } catch (Exception e) {
                        log.error("Error mapping exercise: {}", e.getMessage());
                        // Continue with other exercises
                    }
                }

                // Save batch
                if (!exercises.isEmpty()) {
                    exerciseRepository.saveAll(exercises);
                    totalSeeded += exercises.size();
                    log.info("✓ Seeded {} exercises | Total: {} | Page: {}",
                            exercises.size(), totalSeeded, page + 1);
                }

                // Check metadata for total count
                JsonNode metadata = root.get("metadata");
                if (metadata != null) {
                    JsonNode totalPages = metadata.get("totalPages");
                    JsonNode totalExercises = metadata.get("totalExercises");

                    if (totalExercises != null) {
                        log.info("Progress: {}/{} exercises", totalSeeded, totalExercises.asInt());

                        // Stop if we've seeded all exercises
                        if (totalSeeded >= totalExercises.asInt()) {
                            log.info("All exercises seeded!");
                            break;
                        }
                    }
                }

                // Move to next page
                offset += LIMIT;

                // Small delay to be nice to the API
                Thread.sleep(200);

            } catch (Exception e) {
                log.error("Error seeding exercises at offset {}: {}", offset, e.getMessage(), e);

                // Try to continue with next batch
                offset += LIMIT;

                // If we get too many errors, stop
                emptyResponseCount++;
                if (emptyResponseCount >= 5) {
                    log.error("Too many errors. Stopping seeder.");
                    break;
                }
            }
        }

        log.info("═══════════════════════════════════════");
        log.info("Seeding Summary:");
        log.info("Total exercises seeded: {}", totalSeeded);
        log.info("Final database count: {}", exerciseRepository.count());
        log.info("═══════════════════════════════════════");
    }

    private Exercise mapToExercise(JsonNode node) {
        return Exercise.builder()
                .exerciseId(node.get("exerciseId").asText())
                .name(node.get("name").asText())
                .imageUrl(node.path("gifUrl").asText(null))
                .videoUrl(null)
                .overview(null)
                .equipments(jsonArrayToList(node.path("equipments")))
                .bodyParts(jsonArrayToList(node.path("bodyParts")))
                .targetMuscles(jsonArrayToList(node.path("targetMuscles")))
                .secondaryMuscles(jsonArrayToList(node.path("secondaryMuscles")))
                .keywords(new ArrayList<>())
                .instructions(jsonArrayToList(node.path("instructions")))
                .exerciseTips(new ArrayList<>())
                .variations(new ArrayList<>())
                .relatedExerciseIds(new ArrayList<>())
                .type(ExerciseType.PRELOADED)
                .build();
    }

    private List<String> jsonArrayToList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return new ArrayList<>();
        }

        return StreamSupport.stream(arrayNode.spliterator(), false)
                .map(JsonNode::asText)
                .toList();
    }
}