import { Container, Button } from 'react-bootstrap';
import { Nav }  from 'react-bootstrap';
import { Navbar } from 'react-bootstrap';
import { NavDropdown } from 'react-bootstrap';
import "../css/Navbar.css"
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

type UserDto = {
    username: string;
    email: string;
    name: string;
    creationDate: string;
    roles: string[];
};

const  CollapsibleExample = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<UserDto | null>(null);

    useEffect(() => {
        fetch("http://localhost:8080/info/me", {
            method: "GET",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Unauthorized or error: " + res.status);
                }
                return res.json();
            })
            .then(userData => {
                console.log(userData);
                setUser(userData);
            })
            .catch(() => ("Error user"));
    }, []);


    const handleLogout = () =>{
        fetch("http://localhost:8080/info/logout",{
            method: "POST",
            credentials: "include"
        })
            .then(() =>{
                navigate("/");
            })
            .catch(err =>{
                console.log(err);
            })
    };

    return (
        <Navbar collapseOnSelect expand="lg" className="bg-body-tertiary sticky-navbar">
            <Container fluid>
                <Navbar.Brand href="/mainPage">bookFlow</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">
                        <NavDropdown title="Moje konto" id="collapsible-nav-dropdown">
                            <NavDropdown.Item href="#action/3.1">
                                Edytuj Profil
                            </NavDropdown.Item>
                            <NavDropdown.Item href="#action/3.2">
                                Zmień hasło
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="#action/3.4">
                                Statystyki profilu
                            </NavDropdown.Item>
                        </NavDropdown>
                        <NavDropdown title="Książki" id="collapsible-nav-dropdown">
                            <NavDropdown.Item href="#action/3.1">
                                Wypożycz
                            </NavDropdown.Item>
                            <NavDropdown.Item href="#action/3.2">
                               Zwróć
                            </NavDropdown.Item>
                            <NavDropdown.Item href="#action/3.3">
                                Przedłuż termin oddania
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="#action/3.4">
                                Historia wypożyczeń
                            </NavDropdown.Item>
                        </NavDropdown>
                        {user?.roles?.includes("ADMIN") && (
                            <Nav.Link href="#">[Admin] raporty i statystyki</Nav.Link>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <NavDropdown title="[Admin] Użytkownicy" id="collapsible-nav-dropdown">
                                <NavDropdown.Item href="#action/3.1">
                                    Lista użytkowników
                                </NavDropdown.Item>
                                <NavDropdown.Item href="#action/3.2">
                                    Zarządzaj kontami
                                </NavDropdown.Item>
                            </NavDropdown>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <NavDropdown title="[Admin] Książki" id="collapsible-nav-dropdown">
                                <NavDropdown.Item href="#action/3.1">
                                    Dodaj nową książkę
                                </NavDropdown.Item>
                                <NavDropdown.Item href="#action/3.2">
                                    Edytuj książkę
                                </NavDropdown.Item>
                                <NavDropdown.Item href="#action/3.3">
                                    Usuń książkę
                                </NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item href="#action/3.4">
                                    Ranking Wypożyczeń
                                </NavDropdown.Item>
                            </NavDropdown>
                        )}
                    </Nav>
                    <Button
                        variant="outline-success"
                        onClick={handleLogout}
                    >
                        Wyloguj się
                    </Button>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default CollapsibleExample;