import { useEffect, useMemo, useState } from "react";
import Navbar from "./navbar/Navbar.jsx";
import { Link } from "react-router-dom";
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
      <div className="album py-5 bg-light">
        <div className="container">
          <div className="row mb-4">
            <div className="col-md-6">
              <select
                className="form-select"
                onChange={(e) => setSelectedAuthor(e.target.value)}
              >
                <option value="">All authors</option>
                {allCreators.map((creator) => (
                  <option key={creator} value={creator}>
                    {creator}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
            {filteredprojects.map((project) => (
              <div className="col" key={project.uuid}>
                <Link
                  to={`/dashboard/${project.uuid}`}
                  className="text-decoration-none text-dark"
                //   onClick={(e) => {console.log(project.uuid)}}
                >
                  <div className="card shadow-sm h-100 btn btn-light">
                    <div className="card-body d-flex flex-column">
                      <h5 className="card-title">{project.name}</h5>

                      <p className="card-text">
                        {getSummary(project.description)}
                      </p>

                      <div className="mt-auto">
                        <small className="text-muted d-block">
                          By user: {project.creator.username}
                        </small>

                        <small className="text-muted d-block mb-3">
                          Created at:{" "}
                          {new Date(project.createTime).toLocaleString()}
                        </small>
                      </div>
                    </div>
                  </div>
                </Link>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

export default HomePage;
