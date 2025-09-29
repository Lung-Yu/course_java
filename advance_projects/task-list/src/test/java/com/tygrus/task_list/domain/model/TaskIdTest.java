package com.tygrus.task_list.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

/**
 * TDD測試 - TaskId值物件
 * 
 * 測試TaskId作為核心值物件的所有行為：
 * - 唯一性和不可變性
 * - 相等性和雜湊碼
 * - 驗證邏輯
 * - 字串表示
 */
@DisplayName("TaskId值物件測試")
class TaskIdTest {

    @Nested
    @DisplayName("TaskId創建測試")
    class TaskIdCreationTests {

        @Test
        @DisplayName("應該成功生成TaskId - 當使用generate()方法時")
        void shouldGenerateTaskId_whenUsingGenerateMethod() {
            // Act
            TaskId taskId = TaskId.generate();

            // Assert
            assertThat(taskId).isNotNull();
            assertThat(taskId.getValue()).isNotNull();
            assertThat(taskId.getValue()).isNotEmpty();
            assertThat(taskId.getValue()).hasSize(36); // UUID格式長度
            assertThat(taskId.getValue()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("應該生成唯一的TaskId - 當多次調用generate()時")
        void shouldGenerateUniqueTaskIds_whenCallingGenerateMultipleTimes() {
            // Act
            TaskId taskId1 = TaskId.generate();
            TaskId taskId2 = TaskId.generate();
            TaskId taskId3 = TaskId.generate();

            // Assert
            assertThat(taskId1).isNotEqualTo(taskId2);
            assertThat(taskId2).isNotEqualTo(taskId3);
            assertThat(taskId1).isNotEqualTo(taskId3);
            
            // 確保值也不同
            assertThat(taskId1.getValue()).isNotEqualTo(taskId2.getValue());
            assertThat(taskId2.getValue()).isNotEqualTo(taskId3.getValue());
            assertThat(taskId1.getValue()).isNotEqualTo(taskId3.getValue());
        }

        @Test
        @DisplayName("應該成功創建TaskId - 當提供有效字串時")
        void shouldCreateTaskId_whenValidStringProvided() {
            // Arrange
            String validUuid = "550e8400-e29b-41d4-a716-446655440000";

            // Act
            TaskId taskId = TaskId.of(validUuid);

            // Assert
            assertThat(taskId.getValue()).isEqualTo(validUuid);
        }

        @Test
        @DisplayName("應該成功創建TaskId - 當提供有前後空白的有效字串時")
        void shouldCreateTaskId_whenValidStringWithWhitespaceProvided() {
            // Arrange
            String validUuidWithWhitespace = "  550e8400-e29b-41d4-a716-446655440000  ";
            String expectedValue = "550e8400-e29b-41d4-a716-446655440000";

            // Act
            TaskId taskId = TaskId.of(validUuidWithWhitespace);

            // Assert
            assertThat(taskId.getValue()).isEqualTo(expectedValue);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n", "\r\n"})
        @DisplayName("應該拋出例外 - 當提供null或空白字串時")
        void shouldThrowException_whenNullOrBlankStringProvided(String invalidValue) {
            // Act & Assert
            assertThatThrownBy(() -> TaskId.of(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("TaskId value cannot be (null|empty)");
        }
    }

    @Nested
    @DisplayName("TaskId相等性測試")
    class TaskIdEqualityTests {

        @Test
        @DisplayName("應該相等 - 當兩個TaskId有相同值時")
        void shouldBeEqual_whenTwoTaskIdsHaveSameValue() {
            // Arrange
            String sameValue = "550e8400-e29b-41d4-a716-446655440000";
            TaskId taskId1 = TaskId.of(sameValue);
            TaskId taskId2 = TaskId.of(sameValue);

            // Assert
            assertThat(taskId1).isEqualTo(taskId2);
            assertThat(taskId1.hashCode()).isEqualTo(taskId2.hashCode());
            assertThat(taskId1.equals(taskId2)).isTrue();
            assertThat(taskId2.equals(taskId1)).isTrue(); // 對稱性
        }

        @Test
        @DisplayName("應該不相等 - 當兩個TaskId有不同值時")
        void shouldNotBeEqual_whenTwoTaskIdsHaveDifferentValues() {
            // Arrange
            TaskId taskId1 = TaskId.of("550e8400-e29b-41d4-a716-446655440000");
            TaskId taskId2 = TaskId.of("550e8400-e29b-41d4-a716-446655440001");

            // Assert
            assertThat(taskId1).isNotEqualTo(taskId2);
            assertThat(taskId1.equals(taskId2)).isFalse();
            assertThat(taskId2.equals(taskId1)).isFalse(); // 對稱性
        }

        @Test
        @DisplayName("應該與自己相等 - 反射性測試")
        void shouldEqualItself_reflexivityTest() {
            // Arrange
            TaskId taskId = TaskId.generate();

            // Assert
            assertThat(taskId).isEqualTo(taskId);
            assertThat(taskId.equals(taskId)).isTrue();
        }

        @Test
        @DisplayName("應該不等於null - null安全測試")
        void shouldNotEqualNull_nullSafetyTest() {
            // Arrange
            TaskId taskId = TaskId.generate();

            // Assert
            assertThat(taskId).isNotEqualTo(null);
            assertThat(taskId.equals(null)).isFalse();
        }

        @Test
        @DisplayName("應該不等於其他類型物件 - 類型安全測試")
        void shouldNotEqualDifferentType_typeSafetyTest() {
            // Arrange
            TaskId taskId = TaskId.generate();
            String stringValue = taskId.getValue();

            // Assert
            assertThat(taskId).isNotEqualTo(stringValue);
            assertThat(taskId.equals(stringValue)).isFalse();
        }

        @Test
        @DisplayName("應該滿足傳遞性 - 如果A=B且B=C，則A=C")
        void shouldSatisfyTransitivity_equalityTest() {
            // Arrange
            String sameValue = "550e8400-e29b-41d4-a716-446655440000";
            TaskId taskIdA = TaskId.of(sameValue);
            TaskId taskIdB = TaskId.of(sameValue);
            TaskId taskIdC = TaskId.of(sameValue);

            // Assert
            assertThat(taskIdA).isEqualTo(taskIdB);
            assertThat(taskIdB).isEqualTo(taskIdC);
            assertThat(taskIdA).isEqualTo(taskIdC); // 傳遞性
        }
    }

    @Nested
    @DisplayName("TaskId字串表示測試")
    class TaskIdStringRepresentationTests {

        @Test
        @DisplayName("toString應該返回值 - 當調用toString()時")
        void toStringShouldReturnValue_whenCallingToString() {
            // Arrange
            String value = "550e8400-e29b-41d4-a716-446655440000";
            TaskId taskId = TaskId.of(value);

            // Act & Assert
            assertThat(taskId.toString()).isEqualTo(value);
        }

        @Test
        @DisplayName("toString應該與getValue()結果相同")
        void toStringShouldMatchGetValue() {
            // Arrange
            TaskId taskId = TaskId.generate();

            // Act & Assert
            assertThat(taskId.toString()).isEqualTo(taskId.getValue());
        }
    }

    @Nested
    @DisplayName("TaskId不可變性測試")
    class TaskIdImmutabilityTests {

        @Test
        @DisplayName("應該是不可變的 - getValue()總是返回相同值")
        void shouldBeImmutable_getValueAlwaysReturnsSameValue() {
            // Arrange
            TaskId taskId = TaskId.generate();
            String firstCall = taskId.getValue();

            // Act
            String secondCall = taskId.getValue();
            String thirdCall = taskId.getValue();

            // Assert
            assertThat(secondCall).isEqualTo(firstCall);
            assertThat(thirdCall).isEqualTo(firstCall);
        }

        @Test
        @DisplayName("hashCode應該穩定 - 多次調用返回相同值")
        void hashCodeShouldBeStable_multipleCallsReturnSameValue() {
            // Arrange
            TaskId taskId = TaskId.generate();
            int firstHashCode = taskId.hashCode();

            // Act
            int secondHashCode = taskId.hashCode();
            int thirdHashCode = taskId.hashCode();

            // Assert
            assertThat(secondHashCode).isEqualTo(firstHashCode);
            assertThat(thirdHashCode).isEqualTo(firstHashCode);
        }
    }
}