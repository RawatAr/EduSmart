// Real-Time Notifications - WebSocket (STOMP + SockJS)
let stompClient = null;
let notificationCount = 0;

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('✅ WebSocket Connected');
        stompClient.subscribe('/user/queue/notifications', function(notification) {
            handleNotification(JSON.parse(notification.body));
        });
        fetchUnreadCount();
    }, function(error) {
        console.error('WebSocket error:', error);
        setTimeout(connectWebSocket, 5000);
    });
}

function handleNotification(notification) {
    notificationCount++;
    updateNotificationBadge();
    window.EduSmart?.showToast(notification.message, 'info');
}

async function fetchUnreadCount() {
    try {
        const response = await fetch('/api/notifications/unread-count');
        notificationCount = await response.json();
        updateNotificationBadge();
    } catch (error) {
        console.error('Error fetching notifications:', error);
    }
}

function updateNotificationBadge() {
    const badge = document.getElementById('notificationCount');
    if (badge) {
        badge.textContent = notificationCount > 99 ? '99+' : notificationCount;
        badge.style.display = notificationCount > 0 ? 'inline-block' : 'none';
    }
}

// Auto-connect on page load
document.addEventListener('DOMContentLoaded', connectWebSocket);
