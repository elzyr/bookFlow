import React, { useEffect } from "react";
import "../css/Notification.css"; // zakładam że CSS już masz

interface NotificationProps {
    message: string;
    type?: "success" | "error";
    onClose: () => void;
}

const Notification: React.FC<NotificationProps> = ({ message, type = "success", onClose }) => {
    useEffect(() => {
        const timer = setTimeout(onClose, 3000);
        return () => clearTimeout(timer);
    }, [onClose]);

    return (
        <div className={`notification ${type}`}>
            {message}
        </div>
    );
};

export default Notification;
