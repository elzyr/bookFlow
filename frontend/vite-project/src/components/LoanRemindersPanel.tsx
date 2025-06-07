import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh";
import Notification from "../components/Notification";
import "../css/LoanRemindersPanel.css";

interface LoanDto {
    id: number;
    title: string;
    userEmail: string;
    returnDate: string;
}

const LoanRemindersPanel = () => {
    const [loans, setLoans] = useState<LoanDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [notif, setNotif] =
        useState<{ message: string; type?: "success" | "error" } | null>(null);
    const MS_IN_DAY = 1000 * 60 * 60 * 24; // ms in one day

    const loadLoans = async () => {
        try {
            const res = await fetchWithRefresh(
                "http://localhost:8080/loans/reminder-list",
                { credentials: "include" }
            );
            if (!res.ok) throw new Error();
            setLoans(await res.json());
            setNotif(null);
        } catch {
            setNotif({ message: "Błąd podczas pobierania danych.", type: "error" });
        }
    };

    useEffect(() => {
        loadLoans();
    }, []);


    const sendReminders = async () => {
        if (loading) return;
        setLoading(true);

        try {
            const res = await fetchWithRefresh("http://localhost:8080/email", {
                method: "POST",
                credentials: "include",
            });
            if (!res.ok) throw new Error();

            await loadLoans();
            setNotif({ message: "Przypomnienia wysłane.", type: "success" });
            setTimeout(() => setNotif(null), 4000);
        } catch {
            setNotif({ message: "Błąd przy wysyłaniu przypomnień.", type: "error" });
        } finally {
            setLoading(false);
        }
    };

    const daysLeft = (d: string) =>
        Math.ceil((new Date(d).getTime() - Date.now()) / MS_IN_DAY);

    return (
        <div className="reminders-wrapper">
            <h2 className="panel-title">Wypożyczenia do zwrotu w ciągu 3 dni</h2>

            {loans.length > 0 && (
                <button
                    onClick={sendReminders}
                    disabled={loading}
                    className={`send-btn ${loading ? "loading" : ""}`}
                >
                    {loading ? "Wysyłanie…" : "Wyślij przypomnienia"}
                </button>
            )}

            {loans.length === 0 ? (
                <p className="no-loans">Brak wypożyczeń wymagających przypomnienia.</p>
            ) : (
                <table className="reminders-table">
                    <thead>
                    <tr>
                        <th>E-mail</th>
                        <th>Tytuł książki</th>
                        <th>Dni do zwrotu</th>
                    </tr>
                    </thead>
                    <tbody>
                    {loans.map((l) => (
                        <tr key={l.id}>
                            <td>{l.userEmail}</td>
                            <td>{l.title}</td>
                            <td>{daysLeft(l.returnDate)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            {notif && (
                <Notification
                    message={notif.message}
                    type={notif.type}
                    onClose={() => setNotif(null)}
                />
            )}
        </div>
    );
};

export default LoanRemindersPanel;
