import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh";
import { UserDto } from "../types/UserDto.tsx";
import { useLocation } from "react-router-dom";


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
    refreshUser(): void {
    },
    user: null,
    setUser: () => {},
    loading: true
});

export const UserProvider = ({ children }: UserProviderProps) => {
    const [user, setUser] = useState<UserDto | null>(null);
    const [loading, setLoading] = useState(true);
    const location = useLocation();


    useEffect(() => {
        const unauthenticatedRoutes = ["/login", "/register"];
        const isPublicRoute = unauthenticatedRoutes.some(route => location.pathname.startsWith(route));


        const fetchUser = async () => {
            setLoading(true);
            try {
                const res = await fetchWithRefresh("http://localhost:8080/users/me", {
                    method: "GET",
                    credentials: "include"
                });

                if (!res) {
                    setUser(null);
                    return;
                }

                const data = await res.json();
                setUser(data);
            } catch (err) {
                if (isPublicRoute) {
                    console.error("Błąd pobierania użytkownika:", err);
                }
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, [location.pathname]);


    const refreshUser = () => {
        setLoading(true);
        fetchWithRefresh("http://localhost:8080/users/me", {
            method: "GET",
            credentials: "include",
        })
            .then((res) => {
                if (!res) throw new Error(`HTTP error! status: ${res}`);
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
