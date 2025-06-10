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
                <Navbar.Brand href="/main">bookFlow</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">
                        <NavDropdown title="Książki" id="collapsible-nav-dropdown">
                            <NavDropdown.Item href="/books/info">
                                Wypożycz
                            </NavDropdown.Item>
                            <NavDropdown.Item href="/loans">
                               Zwróć / przedłuż termin
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="/loans/history">
                                Historia wypożyczeń
                            </NavDropdown.Item>
                        </NavDropdown>
                        {user?.roles?.includes("ADMIN") && (
                            <NavDropdown title="[Admin] Książki" id="collapsible-nav-dropdown">
                                <NavDropdown.Item href="/books/add">
                                    Dodaj nową książkę
                                </NavDropdown.Item>
                                <NavDropdown.Item href="/books/edit">
                                    Edytuj książki
                                </NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item href="/stats">
                                    Ranking Wypożyczeń
                                </NavDropdown.Item>
                            </NavDropdown>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <Nav.Link href="/users">[Admin] użytkownicy</Nav.Link>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <Nav.Link href="/loans/verify">[Admin] Wypożyczenia</Nav.Link>
                        )}
                        {user?.roles?.includes("ADMIN") && (
                            <Nav.Link href="/loans/reminder">[Admin] Przypomnienia</Nav.Link>
                        )}
                    </Nav>
                    {user && (
                        <Link to="/profile" className="navbar-email me-3 text-muted" style={{ textDecoration: 'none' }}>
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