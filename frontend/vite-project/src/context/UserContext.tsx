import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh";
import { UserDto } from "../types/UserDto.tsx";

interface UserContextType {
    user: UserDto | null;
    setUser: React.Dispatch<React.SetStateAction<UserDto | null>>;
    loading: boolean;
    refreshUser: () => void;
}

interface UserProviderProps {
    children: ReactNode;
}

const UserContext = createContext<UserContextType>({
    user: null,
    setUser: () => {},
    loading: true
});

export const UserProvider = ({ children }: UserProviderProps) => {
    const [user, setUser] = useState<UserDto | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/info/me", {
            method: "GET",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then((data: UserDto) => setUser(data))
            .catch((err) => {
                console.error("Błąd pobierania użytkownika:", err);
                setUser(null);
            })
            .finally(() => setLoading(false));
    }, []);

    const refreshUser = () => {
        setLoading(true);
        fetchWithRefresh("http://localhost:8080/info/me", {
            method: "GET",
            credentials: "include",
        })
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
                return res.json();
            })
            .then((data: UserDto) => setUser(data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    };

    return (
        <UserContext.Provider value={{ user, setUser, loading, refreshUser }}>
        {children}
        </UserContext.Provider>
    );
};

export const useUser = () => useContext(UserContext);
