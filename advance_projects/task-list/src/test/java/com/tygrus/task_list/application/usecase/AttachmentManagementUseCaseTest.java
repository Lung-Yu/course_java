package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.AttachmentDownloadResponse;
import com.tygrus.task_list.application.dto.AttachmentResponse;
import com.tygrus.task_list.application.dto.AttachmentUploadRequest;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskAttachmentRepository;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AttachmentManagementUseCase 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AttachmentManagementUseCase 測試")
class AttachmentManagementUseCaseTest {

    @Mock
    private TaskAttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    private AttachmentManagementUseCase attachmentManagementUseCase;

    @BeforeEach
    void setUp() {
        attachmentManagementUseCase = new AttachmentManagementUseCase(
                attachmentRepository, taskRepository);
    }

    @Nested
    @DisplayName("上傳附件測試")
    class UploadAttachmentTest {

        @Test
        @DisplayName("應該成功上傳有效的附件")
        void shouldUploadValidAttachment() {
            // Arrange
            String taskId = "task-1";
            String filename = "test.jpg";
            String contentType = "image/jpeg";
            byte[] fileData = "test image data".getBytes();
            
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, filename, contentType, (long) fileData.length, fileData);
            
            TaskAttachment mockAttachment = TaskAttachment.builder()
                    .id(AttachmentId.generate())
                    .taskId(TaskId.of(taskId))
                    .filename(filename)
                    .contentType(contentType)
                    .fileSize(fileData.length)
                    .fileData(fileData)
                    .build();

            when(taskRepository.existsById(TaskId.of(taskId))).thenReturn(true);
            when(attachmentRepository.countByTaskId(TaskId.of(taskId))).thenReturn(0L);
            when(attachmentRepository.findByTaskIdAndFilename(TaskId.of(taskId), filename))
                    .thenReturn(Optional.empty());
            when(attachmentRepository.save(any(TaskAttachment.class))).thenReturn(mockAttachment);

            // Act
            AttachmentResponse response = attachmentManagementUseCase.uploadAttachment(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getTaskId()).isEqualTo(taskId);
            assertThat(response.getFilename()).isEqualTo(filename);
            assertThat(response.getContentType()).isEqualTo(contentType);
            assertThat(response.getFileSize()).isEqualTo(fileData.length);
            assertThat(response.isImage()).isTrue();
            assertThat(response.getFileExtension()).isEqualTo("jpg");

            verify(attachmentRepository).save(any(TaskAttachment.class));
        }

        @Test
        @DisplayName("當任務不存在時應該拋出異常")
        void shouldThrowExceptionWhenTaskNotExists() {
            // Arrange
            String taskId = "non-existent-task";
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, "test.jpg", "image/jpeg", 100L, new byte[100]);

            when(taskRepository.existsById(TaskId.of(taskId))).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.uploadAttachment(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task not found");
        }

        @Test
        @DisplayName("當檔案過大時應該拋出異常")
        void shouldThrowExceptionWhenFileTooLarge() {
            // Arrange
            String taskId = "task-1";
            long largeFileSize = 51 * 1024 * 1024; // 51MB
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, "large.jpg", "image/jpeg", largeFileSize, new byte[(int) largeFileSize]);

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.uploadAttachment(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("File size exceeds maximum limit");
        }

        @Test
        @DisplayName("當檔案類型不支援時應該拋出異常")
        void shouldThrowExceptionWhenUnsupportedFileType() {
            // Arrange
            String taskId = "task-1";
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, "test.exe", "application/x-executable", 100L, new byte[100]);

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.uploadAttachment(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported file type");
        }

        @Test
        @DisplayName("當超過附件數量限制時應該拋出異常")
        void shouldThrowExceptionWhenExceedsAttachmentLimit() {
            // Arrange
            String taskId = "task-1";
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, "test.jpg", "image/jpeg", 100L, new byte[100]);

            when(taskRepository.existsById(TaskId.of(taskId))).thenReturn(true);
            when(attachmentRepository.countByTaskId(TaskId.of(taskId))).thenReturn(10L);

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.uploadAttachment(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Maximum number of attachments per task exceeded");
        }

        @Test
        @DisplayName("當同名檔案已存在時應該拋出異常")
        void shouldThrowExceptionWhenFileWithSameNameExists() {
            // Arrange
            String taskId = "task-1";
            String filename = "existing.jpg";
            AttachmentUploadRequest request = new AttachmentUploadRequest(
                    taskId, filename, "image/jpeg", 100L, new byte[100]);

            TaskAttachment existingAttachment = TaskAttachment.builder()
                    .id(AttachmentId.generate())
                    .taskId(TaskId.of(taskId))
                    .filename(filename)
                    .contentType("image/jpeg")
                    .fileSize(200L)
                    .fileData(new byte[200])
                    .build();

            when(taskRepository.existsById(TaskId.of(taskId))).thenReturn(true);
            when(attachmentRepository.countByTaskId(TaskId.of(taskId))).thenReturn(1L);
            when(attachmentRepository.findByTaskIdAndFilename(TaskId.of(taskId), filename))
                    .thenReturn(Optional.of(existingAttachment));

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.uploadAttachment(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("File with the same name already exists");
        }
    }

    @Nested
    @DisplayName("下載附件測試")
    class DownloadAttachmentTest {

        @Test
        @DisplayName("應該成功下載存在的附件")
        void shouldDownloadExistingAttachment() {
            // Arrange
            String attachmentId = "attachment-1";
            TaskAttachment attachment = TaskAttachment.builder()
                    .id(AttachmentId.of(attachmentId))
                    .taskId(TaskId.of("task-1"))
                    .filename("test.jpg")
                    .contentType("image/jpeg")
                    .fileSize(100L)
                    .fileData("test data".getBytes())
                    .build();

            when(attachmentRepository.findById(AttachmentId.of(attachmentId)))
                    .thenReturn(Optional.of(attachment));

            // Act
            AttachmentDownloadResponse response = attachmentManagementUseCase.downloadAttachment(attachmentId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getFilename()).isEqualTo("test.jpg");
            assertThat(response.getContentType()).isEqualTo("image/jpeg");
            assertThat(response.getFileSize()).isEqualTo(100L);
            assertThat(response.getFileData()).isEqualTo("test data".getBytes());
        }

        @Test
        @DisplayName("當附件不存在時應該返回錯誤")
        void shouldReturnErrorWhenAttachmentNotExists() {
            // Arrange
            String attachmentId = "non-existent-attachment";
            when(attachmentRepository.findById(AttachmentId.of(attachmentId)))
                    .thenReturn(Optional.empty());

            // Act
            AttachmentDownloadResponse response = attachmentManagementUseCase.downloadAttachment(attachmentId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).contains("Attachment not found");
        }
    }

    @Nested
    @DisplayName("獲取任務附件測試")
    class GetTaskAttachmentsTest {

        @Test
        @DisplayName("應該返回任務的所有附件")
        void shouldReturnAllTaskAttachments() {
            // Arrange
            String taskId = "task-1";
            List<TaskAttachment> attachments = Arrays.asList(
                    TaskAttachment.builder()
                            .id(AttachmentId.of("att-1"))
                            .taskId(TaskId.of(taskId))
                            .filename("file1.jpg")
                            .contentType("image/jpeg")
                            .fileSize(100L)
                            .fileData(new byte[100])
                            .build(),
                    TaskAttachment.builder()
                            .id(AttachmentId.of("att-2"))
                            .taskId(TaskId.of(taskId))
                            .filename("file2.pdf")
                            .contentType("application/pdf")
                            .fileSize(200L)
                            .fileData(new byte[200])
                            .build()
            );

            when(attachmentRepository.findByTaskId(TaskId.of(taskId))).thenReturn(attachments);

            // Act
            List<AttachmentResponse> responses = attachmentManagementUseCase.getTaskAttachments(taskId);

            // Assert
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getFilename()).isEqualTo("file1.jpg");
            assertThat(responses.get(0).isImage()).isTrue();
            assertThat(responses.get(1).getFilename()).isEqualTo("file2.pdf");
            assertThat(responses.get(1).isDocument()).isTrue();
        }

        @Test
        @DisplayName("當任務沒有附件時應該返回空列表")
        void shouldReturnEmptyListWhenNoAttachments() {
            // Arrange
            String taskId = "task-without-attachments";
            when(attachmentRepository.findByTaskId(TaskId.of(taskId))).thenReturn(Arrays.asList());

            // Act
            List<AttachmentResponse> responses = attachmentManagementUseCase.getTaskAttachments(taskId);

            // Assert
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("刪除附件測試")
    class DeleteAttachmentTest {

        @Test
        @DisplayName("應該成功刪除存在的附件")
        void shouldDeleteExistingAttachment() {
            // Arrange
            String attachmentId = "attachment-1";
            when(attachmentRepository.existsById(AttachmentId.of(attachmentId))).thenReturn(true);

            // Act
            attachmentManagementUseCase.deleteAttachment(attachmentId);

            // Assert
            verify(attachmentRepository).deleteById(AttachmentId.of(attachmentId));
        }

        @Test
        @DisplayName("當附件不存在時應該拋出異常")
        void shouldThrowExceptionWhenAttachmentNotExists() {
            // Arrange
            String attachmentId = "non-existent-attachment";
            when(attachmentRepository.existsById(AttachmentId.of(attachmentId))).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> attachmentManagementUseCase.deleteAttachment(attachmentId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Attachment not found");
        }
    }

    @Nested
    @DisplayName("刪除任務附件測試")
    class DeleteTaskAttachmentsTest {

        @Test
        @DisplayName("應該刪除任務的所有附件")
        void shouldDeleteAllTaskAttachments() {
            // Arrange
            String taskId = "task-1";
            when(attachmentRepository.countByTaskId(TaskId.of(taskId))).thenReturn(3L);

            // Act
            attachmentManagementUseCase.deleteTaskAttachments(taskId);

            // Assert
            verify(attachmentRepository).deleteByTaskId(TaskId.of(taskId));
        }
    }

    @Nested
    @DisplayName("獲取附件資訊測試")
    class GetAttachmentInfoTest {

        @Test
        @DisplayName("應該返回存在附件的資訊")
        void shouldReturnAttachmentInfo() {
            // Arrange
            String attachmentId = "attachment-1";
            TaskAttachment attachment = TaskAttachment.builder()
                    .id(AttachmentId.of(attachmentId))
                    .taskId(TaskId.of("task-1"))
                    .filename("test.jpg")
                    .contentType("image/jpeg")
                    .fileSize(100L)
                    .fileData(new byte[100])
                    .build();

            when(attachmentRepository.findById(AttachmentId.of(attachmentId)))
                    .thenReturn(Optional.of(attachment));

            // Act
            Optional<AttachmentResponse> response = attachmentManagementUseCase.getAttachmentInfo(attachmentId);

            // Assert
            assertThat(response).isPresent();
            assertThat(response.get().getFilename()).isEqualTo("test.jpg");
            assertThat(response.get().getContentType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("當附件不存在時應該返回空")
        void shouldReturnEmptyWhenAttachmentNotExists() {
            // Arrange
            String attachmentId = "non-existent-attachment";
            when(attachmentRepository.findById(AttachmentId.of(attachmentId)))
                    .thenReturn(Optional.empty());

            // Act
            Optional<AttachmentResponse> response = attachmentManagementUseCase.getAttachmentInfo(attachmentId);

            // Assert
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("配置測試")
    class ConfigurationTest {

        @Test
        @DisplayName("應該返回支援的檔案類型")
        void shouldReturnSupportedContentTypes() {
            // Act
            var supportedTypes = attachmentManagementUseCase.getSupportedContentTypes();

            // Assert
            assertThat(supportedTypes).isNotEmpty();
            assertThat(supportedTypes).contains("image/jpeg", "application/pdf", "text/plain");
        }

        @Test
        @DisplayName("應該返回最大檔案大小")
        void shouldReturnMaxFileSize() {
            // Act
            long maxSize = attachmentManagementUseCase.getMaxFileSize();

            // Assert
            assertThat(maxSize).isEqualTo(50 * 1024 * 1024); // 50MB
        }

        @Test
        @DisplayName("應該返回每個任務的最大附件數量")
        void shouldReturnMaxAttachmentsPerTask() {
            // Act
            int maxAttachments = attachmentManagementUseCase.getMaxAttachmentsPerTask();

            // Assert
            assertThat(maxAttachments).isEqualTo(10);
        }
    }
}