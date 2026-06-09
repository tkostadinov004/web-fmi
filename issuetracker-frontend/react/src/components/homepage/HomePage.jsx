import { useEffect, useMemo, useState } from "react";
import Navbar from "./navbar/Navbar.jsx";
import { Link } from "react-router-dom";
import { Container, Row, Col, Card, Form } from "react-bootstrap";
//липсват данни за последна модификация; кратко описание отделно,
// списък със продукти, време за приготвяне, автора да бъде с id

// const projects = [
//   {
//     id: "r1",
//     userId: "admin",
//     name: "Banitsa",
//     shortDescription:
//       "Traditional Bulgarian pastry made with eggs, cheese and filo pastry layers.",
//     prepTime: 45,
//     ingredients: ["eggs", "cheese", "phyllo dough", "yogurt"],
//     imageUrl: "https://picsum.photos/400/250?1",
//     fullDescription:
//       "Banitsa is a traditional Bulgarian baked pastry made with layers of filo dough, eggs, and cheese. It is commonly eaten for breakfast and is one of the most popular dishes in Bulgaria.",
//     tags: ["bulgarian", "breakfast"],
//     createdAt: "2026-05-10T10:00:00.000Z",
//     updatedAt: "2026-05-10T10:00:00.000Z",
//   },
//   {
//     id: "r2",
//     userId: "john",
//     name: "Chocolate Cake",
//     shortDescription:
//       "Rich and moist chocolate cake with creamy frosting and soft texture.",
//     prepTime: 90,
//     ingredients: ["flour", "cocoa", "eggs", "sugar", "butter"],
//     imageUrl: "https://picsum.photos/400/250?2",
//     fullDescription:
//       "A classic chocolate cake made with rich cocoa and layered with smooth chocolate frosting. Perfect for celebrations and desserts.",
//     tags: ["dessert"],
//     createdAt: "2026-05-12T12:30:00.000Z",
//     updatedAt: "2026-05-12T12:30:00.000Z",
//   },
//   {
//     id: "r3",
//     userId: "maria",
//     name: "Greek Salad",
//     shortDescription:
//       "Fresh salad with tomatoes, cucumbers, olives, feta cheese and olive oil.",
//     prepTime: 15,
//     ingredients: ["tomatoes", "cucumber", "olives", "feta cheese", "olive oil"],
//     imageUrl: "https://picsum.photos/400/250?3",
//     fullDescription:
//       "Greek salad is a fresh and healthy Mediterranean dish made with vegetables, olives, and feta cheese, dressed with olive oil.",
//     tags: ["salad", "healthy", "mediterranean"],
//     createdAt: "2026-05-15T09:15:00.000Z",
//     updatedAt: "2026-05-15T09:15:00.000Z",
//   },
// ];
const getHeaders = () => {
  const token = localStorage.getItem("authToken");
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
};

function HomePage() {
  const [selectedAuthor, setSelectedAuthor] = useState("");
  const [projects, setProjects] = useState([]);

  useEffect(() => {
    async function loadProjects() {
      try {
        const response = await fetch(`http://localhost:8080/projects`, {
          method: "GET",
          headers: getHeaders(),
        });

        const data = await response.json();

        setProjects(data);
      } catch (error) {
        console.log(error);
      }
    }

    loadProjects();
  }, []);

  const filteredprojects = useMemo(() => {
    return projects
      .filter((r) =>
        selectedAuthor ? r.creator.username === selectedAuthor : true,
      )
      .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      .slice(0, 10);
  }, [projects, selectedAuthor]);

  const getSummary = (text) => {
    if (!text) return "";
    return text.length > 150 ? text.slice(0, 150) + "..." : text;
  };

  const allCreators = useMemo(() => {
    return [...new Set(projects.map((r) => r.creator.username))];
  }, [projects]);

  return (
    <>
      <Navbar> </Navbar>
      <Container className="py-5">
        {" "}
        <Row className="mb-4">
          {" "}
          <Col md={6}>
            {" "}
            <Form.Select
              value={selectedAuthor}
              onChange={(e) => setSelectedAuthor(e.target.value)}
            >
              {" "}
              <option value="">All authors</option>{" "}
              {allCreators.map((creator) => (
                <option key={creator} value={creator}>
                  {" "}
                  {creator}{" "}
                </option>
              ))}{" "}
            </Form.Select>{" "}
          </Col>{" "}
        </Row>{" "}
        <Row xs={1} sm={2} md={3} className="g-3">
          {" "}
          {filteredprojects.map((project) => (
            <Col key={project.uuid}>
              {" "}
              <Link
                to={`/dashboard/${project.uuid}`}
                className="text-decoration-none"
              >
                {" "}
                <Card className="h-100 shadow-sm">
                  {" "}
                  <Card.Body className="d-flex flex-column">
                    {" "}
                    <Card.Title> {project.name} </Card.Title>{" "}
                    <Card.Text> {getSummary(project.description)} </Card.Text>{" "}
                    <div className="mt-auto">
                      {" "}
                      <small className="text-muted d-block">
                        {" "}
                        By user: {project.creator.username}{" "}
                      </small>{" "}
                      <small className="text-muted d-block">
                        {" "}
                        Created at:{" "}
                        {new Date(project.createTime).toLocaleString()}{" "}
                      </small>{" "}
                    </div>{" "}
                  </Card.Body>{" "}
                </Card>{" "}
              </Link>{" "}
            </Col>
          ))}{" "}
        </Row>{" "}
      </Container>
    </>
  );
}

export default HomePage;
