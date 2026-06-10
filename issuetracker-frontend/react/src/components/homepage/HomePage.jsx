import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Container, Row, Col, Card, Form, Button } from "react-bootstrap";

import Navbar from "./navbar/Navbar.jsx";
import CreateProjectPage from "../CreateProjectPage/createProject.jsx";

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
  const [isLoading, setIsLoading] = useState(false);

  const loadProjects = async () => {
    try {
      setIsLoading(true);

      const response = await fetch("/api/projects", {
        method: "GET",
        headers: getHeaders(),
      });

      if (!response.ok) {
        throw new Error("Failed to load projects");
      }

      const data = await response.json();

      setProjects(data);
    } catch (error) {
      console.error("Error loading projects:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const deleteProject = async (projectId) => {
    try {
      const response = await fetch(`/api/projects/${projectId}`, {
        method: "DELETE",
        headers: getHeaders(),
      });

      if (!response.ok) {
        throw new Error("Failed to delete project");
      }

      await loadProjects();
    } catch (error) {
      console.error("Error deleting project:", error);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const filteredProjects = useMemo(() => {
    return projects
      .filter((project) =>
        selectedAuthor
          ? project.creator?.username === selectedAuthor
          : true
      )
      .sort(
        (a, b) =>
          new Date(b.createTime) -
          new Date(a.createTime)
      )
      .slice(0, 10);
  }, [projects, selectedAuthor]);

  const allCreators = useMemo(() => {
    return [
      ...new Set(
        projects
          .map((project) => project.creator?.username)
          .filter(Boolean)
      ),
    ];
  }, [projects]);

  const getSummary = (text) => {
    if (!text) return "";

    return text.length > 150
      ? `${text.slice(0, 150)}...`
      : text;
  };

  if (isLoading) {
    return (
      <>
        <Navbar />
        <Container className="py-5">
          <h5>Loading...</h5>
        </Container>
      </>
    );
  }

  return (
    <>
      <Navbar />

      <Container className="py-5">
        <Row className="mb-4 align-items-center">
          <Col md={6}>
            <Form.Select
              value={selectedAuthor}
              onChange={(e) =>
                setSelectedAuthor(e.target.value)
              }
            >
              <option value="">
                All authors
              </option>

              {allCreators.map((creator) => (
                <option
                  key={creator}
                  value={creator}
                >
                  {creator}
                </option>
              ))}
            </Form.Select>
          </Col>

          <Col className="text-end">
            <CreateProjectPage
              onProjectCreated={loadProjects}
            />
          </Col>
        </Row>

        <Row xs={1} sm={2} md={3} className="g-3">
          {filteredProjects.map((project) => (
            <Col key={project.uuid}>
              <Card className="h-100 shadow-sm">
                <Card.Body className="d-flex flex-column">
                  <Row className="align-items-center mb-2">
                    <Col>
                      <Link
                        to={`/dashboard/${project.uuid}`}
                        className="text-decoration-none text-dark"
                      >
                        <Card.Title>
                          {project.name}
                        </Card.Title>
                      </Link>
                    </Col>

                    <Col xs="auto">
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() =>
                          deleteProject(project.uuid)
                        }
                      >
                        Delete
                      </Button>
                    </Col>
                  </Row>

                  <Link
                    to={`/dashboard/${project.uuid}`}
                    className="text-decoration-none text-dark"
                  >
                    <Card.Text>
                      {getSummary(
                        project.description
                      )}
                    </Card.Text>

                    <div className="mt-auto">
                      <small className="text-muted d-block">
                        By user:{" "}
                        {project.creator?.username ??
                          "Unknown"}
                      </small>

                      <small className="text-muted d-block">
                        Created at:{" "}
                        {new Date(
                          project.createTime
                        ).toLocaleString()}
                      </small>
                    </div>
                  </Link>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>
    </>
  );
}

export default HomePage;