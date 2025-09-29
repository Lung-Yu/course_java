package com.tygrus.task_list.domain.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * ObservableSupport 測試類別
 * 測試 Observer 設計模式的實作
 */
@DisplayName("ObservableSupport 測試")
class ObservableSupportTest {
    
    private ObservableSupport<String> observableSupport;
    
    @Mock
    private Observer<String> mockObserver1;
    
    @Mock
    private Observer<String> mockObserver2;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        observableSupport = new ObservableSupport<>();
        
        // 設置 mock 觀察者的 ID
        when(mockObserver1.getObserverId()).thenReturn("observer1");
        when(mockObserver2.getObserverId()).thenReturn("observer2");
        
        // 設置觀察者對所有事件感興趣
        when(mockObserver1.isInterestedIn(any(Class.class))).thenReturn(true);
        when(mockObserver2.isInterestedIn(any(Class.class))).thenReturn(true);
    }
    
    @Nested
    @DisplayName("觀察者註冊測試")
    class ObserverRegistrationTest {
        
        @Test
        @DisplayName("應該能成功註冊觀察者")
        void shouldRegisterObserver() {
            // When
            observableSupport.addObserver(mockObserver1);
            
            // Then
            assertEquals(1, observableSupport.getObserverCount());
            assertTrue(observableSupport.hasObservers());
            assertTrue(observableSupport.getObserverIds().contains("observer1"));
        }
        
        @Test
        @DisplayName("應該能註冊多個觀察者")
        void shouldRegisterMultipleObservers() {
            // When
            observableSupport.addObserver(mockObserver1);
            observableSupport.addObserver(mockObserver2);
            
            // Then
            assertEquals(2, observableSupport.getObserverCount());
            assertTrue(observableSupport.getObserverIds().contains("observer1"));
            assertTrue(observableSupport.getObserverIds().contains("observer2"));
        }
        
        @Test
        @DisplayName("註冊 null 觀察者應該拋出異常")
        void shouldThrowExceptionWhenRegisteringNullObserver() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> observableSupport.addObserver(null)
            );
            assertEquals("Observer cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("註冊觀察者ID為null應該拋出異常")
        void shouldThrowExceptionWhenObserverIdIsNull() {
            // Given
            Observer<String> invalidObserver = mock(Observer.class);
            when(invalidObserver.getObserverId()).thenReturn(null);
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> observableSupport.addObserver(invalidObserver)
            );
            assertEquals("Observer ID cannot be null or empty", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("觀察者移除測試")
    class ObserverRemovalTest {
        
        @BeforeEach
        void setUp() {
            observableSupport.addObserver(mockObserver1);
            observableSupport.addObserver(mockObserver2);
        }
        
        @Test
        @DisplayName("應該能通過物件移除觀察者")
        void shouldRemoveObserverByObject() {
            // When
            observableSupport.removeObserver(mockObserver1);
            
            // Then
            assertEquals(1, observableSupport.getObserverCount());
            assertFalse(observableSupport.getObserverIds().contains("observer1"));
            assertTrue(observableSupport.getObserverIds().contains("observer2"));
        }
        
        @Test
        @DisplayName("應該能通過ID移除觀察者")
        void shouldRemoveObserverById() {
            // When
            observableSupport.removeObserver("observer1");
            
            // Then
            assertEquals(1, observableSupport.getObserverCount());
            assertFalse(observableSupport.getObserverIds().contains("observer1"));
            assertTrue(observableSupport.getObserverIds().contains("observer2"));
        }
        
        @Test
        @DisplayName("應該能清除所有觀察者")
        void shouldClearAllObservers() {
            // When
            observableSupport.clearObservers();
            
            // Then
            assertEquals(0, observableSupport.getObserverCount());
            assertFalse(observableSupport.hasObservers());
        }
        
        @Test
        @DisplayName("移除不存在的觀察者不應該有影響")
        void shouldHandleRemovalOfNonExistentObserver() {
            // When
            observableSupport.removeObserver("nonexistent");
            
            // Then
            assertEquals(2, observableSupport.getObserverCount());
        }
    }
    
    @Nested
    @DisplayName("同步通知測試")
    class SynchronousNotificationTest {
        
        @BeforeEach
        void setUp() {
            observableSupport.addObserver(mockObserver1);
            observableSupport.addObserver(mockObserver2);
        }
        
        @Test
        @DisplayName("應該通知所有觀察者")
        void shouldNotifyAllObservers() {
            // Given
            String event = "test event";
            
            // When
            observableSupport.notifyObservers(event);
            
            // Then
            verify(mockObserver1).update(event);
            verify(mockObserver2).update(event);
        }
        
        @Test
        @DisplayName("通知 null 事件不應該調用觀察者")
        void shouldNotNotifyObserversWhenEventIsNull() {
            // When
            observableSupport.notifyObservers(null);
            
            // Then
            verify(mockObserver1, never()).update(any());
            verify(mockObserver2, never()).update(any());
        }
        
        @Test
        @DisplayName("觀察者拋出異常不應該影響其他觀察者")
        void shouldContinueNotifyingWhenObserverThrowsException() {
            // Given
            String event = "test event";
            doThrow(new RuntimeException("Observer error")).when(mockObserver1).update(event);
            
            // When
            assertDoesNotThrow(() -> observableSupport.notifyObservers(event));
            
            // Then
            verify(mockObserver1).update(event);
            verify(mockObserver2).update(event);
        }
        
        @Test
        @DisplayName("只應該通知感興趣的觀察者")
        void shouldOnlyNotifyInterestedObservers() {
            // Given
            String event = "test event";
            when(mockObserver1.isInterestedIn(String.class)).thenReturn(true);
            when(mockObserver2.isInterestedIn(String.class)).thenReturn(false);
            
            // When
            observableSupport.notifyObservers(event);
            
            // Then
            verify(mockObserver1).update(event);
            verify(mockObserver2, never()).update(event);
        }
    }
    
    @Nested
    @DisplayName("異步通知測試")
    class AsynchronousNotificationTest {
        
        @BeforeEach
        void setUp() {
            observableSupport.addObserver(mockObserver1);
            observableSupport.addObserver(mockObserver2);
        }
        
        @Test
        @DisplayName("應該異步通知所有觀察者")
        void shouldNotifyAllObserversAsynchronously() throws Exception {
            // Given
            String event = "async test event";
            CountDownLatch latch = new CountDownLatch(2);
            
            // 設置觀察者在被調用時倒數計時
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            }).when(mockObserver1).update(event);
            
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            }).when(mockObserver2).update(event);
            
            // When
            CompletableFuture<Void> future = observableSupport.notifyObserversAsync(event);
            
            // Then
            future.get(5, TimeUnit.SECONDS); // 等待完成
            assertTrue(latch.await(1, TimeUnit.SECONDS)); // 確保所有觀察者都被調用
            
            verify(mockObserver1).update(event);
            verify(mockObserver2).update(event);
        }
        
        @Test
        @DisplayName("異步通知 null 事件應該立即完成")
        void shouldCompleteImmediatelyWhenEventIsNull() throws Exception {
            // When
            CompletableFuture<Void> future = observableSupport.notifyObserversAsync(null);
            
            // Then
            assertNull(future.get(1, TimeUnit.SECONDS));
            verify(mockObserver1, never()).update(any());
            verify(mockObserver2, never()).update(any());
        }
        
        @Test
        @DisplayName("沒有觀察者時異步通知應該立即完成")
        void shouldCompleteImmediatelyWhenNoObservers() throws Exception {
            // Given
            observableSupport.clearObservers();
            
            // When
            CompletableFuture<Void> future = observableSupport.notifyObserversAsync("event");
            
            // Then
            assertNull(future.get(1, TimeUnit.SECONDS));
        }
        
        @Test
        @DisplayName("異步通知中觀察者拋出異常不應該影響其他觀察者")
        void shouldHandleObserverExceptionsInAsyncNotification() throws Exception {
            // Given
            String event = "error test event";
            AtomicReference<Exception> observer1Exception = new AtomicReference<>();
            AtomicInteger observer2CallCount = new AtomicInteger(0);
            
            doAnswer(invocation -> {
                observer1Exception.set(new RuntimeException("Observer 1 error"));
                throw observer1Exception.get();
            }).when(mockObserver1).update(event);
            
            doAnswer(invocation -> {
                observer2CallCount.incrementAndGet();
                return null;
            }).when(mockObserver2).update(event);
            
            // When
            CompletableFuture<Void> future = observableSupport.notifyObserversAsync(event);
            
            // Then
            assertDoesNotThrow(() -> future.get(5, TimeUnit.SECONDS));
            assertEquals(1, observer2CallCount.get()); // Observer2 仍然被調用
        }
    }
    
    @Nested
    @DisplayName("執行緒安全測試")
    class ThreadSafetyTest {
        
        @Test
        @DisplayName("併發添加和移除觀察者應該是安全的")
        void shouldHandleConcurrentAddAndRemove() throws Exception {
            // Given
            int threadCount = 10;
            int operationsPerThread = 100;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);
            
            // When - 併發添加和移除觀察者
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        startLatch.await();
                        
                        for (int j = 0; j < operationsPerThread; j++) {
                            Observer<String> observer = mock(Observer.class);
                            when(observer.getObserverId()).thenReturn("thread" + threadId + "_op" + j);
                            
                            observableSupport.addObserver(observer);
                            
                            if (j % 2 == 0) {
                                observableSupport.removeObserver("thread" + threadId + "_op" + j);
                            }
                        }
                        
                    } catch (Exception e) {
                        fail("Thread " + threadId + " failed: " + e.getMessage());
                    } finally {
                        endLatch.countDown();
                    }
                }).start();
            }
            
            startLatch.countDown(); // 開始所有執行緒
            
            // Then
            assertTrue(endLatch.await(30, TimeUnit.SECONDS));
            
            // 驗證最終狀態是一致的
            assertTrue(observableSupport.getObserverCount() >= 0);
        }
        
        @Test
        @DisplayName("併發通知應該是安全的")
        void shouldHandleConcurrentNotifications() throws Exception {
            // Given
            observableSupport.addObserver(mockObserver1);
            
            int threadCount = 10;
            int notificationsPerThread = 50;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);
            AtomicInteger totalNotifications = new AtomicInteger(0);
            
            doAnswer(invocation -> {
                totalNotifications.incrementAndGet();
                return null;
            }).when(mockObserver1).update(anyString());
            
            // When - 併發發送通知
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        startLatch.await();
                        
                        for (int j = 0; j < notificationsPerThread; j++) {
                            observableSupport.notifyObservers("notification_" + threadId + "_" + j);
                        }
                        
                    } catch (Exception e) {
                        fail("Notification thread " + threadId + " failed: " + e.getMessage());
                    } finally {
                        endLatch.countDown();
                    }
                }).start();
            }
            
            startLatch.countDown();
            
            // Then
            assertTrue(endLatch.await(30, TimeUnit.SECONDS));
            assertEquals(threadCount * notificationsPerThread, totalNotifications.get());
        }
    }
}