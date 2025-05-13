import { Container, Button } from 'react-bootstrap';
import { Nav }  from 'react-bootstrap';
import { Navbar } from 'react-bootstrap';
import { NavDropdown } from 'react-bootstrap';
import "../css/Navbar.css"
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import { User} from 'lucide-react';
import { Link } from "react-router-dom";

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
        fetch("http://localhost:8080/users/me", {
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
        fetch("http://localhost:8080/users/logout",{
            method: "POST",
            credentials: "include"
        })
            .then(() =>{
                setUser(null);
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
                        <NavDropdown title="Książki" id="collapsible-nav-dropdown">
                            <NavDropdown.Item href="/bookPage">
                                Wypożycz
                            </NavDropdown.Item>
                            <NavDropdown.Item href="/bookStatus">
                               Zwróć / przedłuż termin
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="/loanUserInfo">
                                Historia wypożyczeń
                            </NavDropdown.Item>
                        </NavDropdown>
                        {user?.roles?.includes("ADMIN") && (
                            <NavDropdown title="[Admin] Książki" id="collapsible-nav-dropdown">
                                <NavDropdown.Item href="/addBook">
                                    Dodaj nową książkę
                                </NavDropdown.Item>
                                <NavDropdown.Item href="/adminbookpage">
                                    Edytuj książki
                                </NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item href="/AdminRanks">
                                    Ranking Wypożyczeń
                                </NavDropdown.Item>
                            </NavDropdown>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <Nav.Link href="/adminUserPage">[Admin] użytkownicy</Nav.Link>
                        )}
                    </Nav>
                    {user && (
                        <Link to="/userInfo" className="navbar-email me-3 text-muted" style={{ textDecoration: 'none' }}>
                            <User size={18} />
                            <span>
                                <strong>{user.email}</strong>
                            </span>
                        </Link>
                    )}

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