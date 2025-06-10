import React, { useEffect } from "react";
import "../css/Notification.css";

interface NotificationProps {
    message: string;
    type?: "success" | "error";
    onClose: () => void;
}

const Notification: React.FC<NotificationProps> = ({ message, type = "success", onClose }) => {
    useEffect(() => {
        const timer = setTimeout(onClose, 5000);
        return () => clearTimeout(timer);
    }, [onClose]);

    return (
        <div className={`notification ${type}`}>
            {message}
        </div>
    );
};

export default Notification;
