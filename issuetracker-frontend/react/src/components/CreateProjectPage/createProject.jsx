// import { useState } from "react";
// import Dropdown from "react-bootstrap/Dropdown";
// import Modal from "react-bootstrap/Modal";
// import Form from "react-bootstrap/Form";
// import Button from "react-bootstrap/Button";
// import WorkflowEditor from "../TicketDetail/WorkflowEditor";

// const API_BASE = "/api";

// const getHeaders = () => {
//   const token = localStorage.getItem("authToken");
//   return {
//     "Content-Type": "application/json",
//     Authorization: `Bearer ${token}`,
//   };
// };

// function CreateProjectPage() {
//   const [showModal, setShowModal] = useState(false);
//   const [errorMessage, setErrorMessage] = useState(false);

//   const [form, setForm] = useState({
//     name: "",
//   });

//   const handleClose = () => {
//     setShowModal(false);
//     setErrorMessage("")
//   };

//   const handleOpen = () => {
//     setShowModal(true);
//   };

//   const handleChange = (e) => {
//     setForm({
//       ...form,
//       [e.target.name]: e.target.value,
//     });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     try {
//       console.log(form);

//       const response = await fetch(`${API_BASE}/projects`, {
//         method: "POST",
//         headers: getHeaders(),
//         body: JSON.stringify(form)
//       });

//       if (response.ok) {
//         console.log("Succesful created project");
//         setShowModal(false);
//       } else {
//         setErrorMessage("Не успяхме да заредим информацията за билета.");
//       }
//     } catch (error) {
//       console.error(error);
//     }
//   };

//   return (
//     <>
//       <Dropdown.Item
//         onClick={(e) => {
//           e.preventDefault();
//           handleOpen();
//         }}
//       >
//         Create new project...
//       </Dropdown.Item>

//       <Modal show={showModal} onHide={handleClose} centered>
//         <Form onSubmit={handleSubmit}>
//           <Modal.Header closeButton>
//             <Modal.Title>Create Project</Modal.Title>
//           </Modal.Header>

//           <Modal.Body>
//             {errorMessage && <div>{errorMessage}</div>}

//             <Form.Group className="mb-3">
//               <Form.Label>Project Name</Form.Label>

//               <Form.Control
//                 type="text"
//                 name="name"
//                 value={form.name}
//                 onChange={handleChange}
//                 required
//               />
//             </Form.Group>
//           </Modal.Body>

//           <Modal.Footer>
//             <Button variant="secondary" onClick={handleClose}>
//               Cancel
//             </Button>

//             <Button variant="primary" type="submit">
//               Create
//             </Button>
//           </Modal.Footer>
//         </Form>
//       </Modal>
//     </>
//   );
// }

// export default CreateProjectPage;

import { useState } from "react";
import Dropdown from "react-bootstrap/Dropdown";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Alert from "react-bootstrap/Alert";

import WorkflowEditor from "../TicketDetail/WorkflowEditor";

const API_BASE = "/api";

const getHeaders = () => {
  const token = localStorage.getItem("authToken");

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
};

function CreateProjectPage({ onProjectCreated }) {
  const [showModal, setShowModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const [step, setStep] = useState("project");
  const [projectId, setProjectId] = useState(null);

  const [form, setForm] = useState({
    name: "",
  });

  const handleOpen = () => {
    setShowModal(true);
  };

  const handleClose = () => {
    console.log("On Close is called");
    setShowModal(false);

    setErrorMessage("");
    setStep("project");
    setProjectId(null);

    setForm({
      name: "",
    });
    onProjectCreated(projectId);
  };

  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setErrorMessage("");

      const response = await fetch(`${API_BASE}/projects`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        setErrorMessage("Failed to create project.");
        return;
      }

      const createdProject = await response.json();

      setProjectId(createdProject.uuid);

      setStep("workflow");
    } catch (error) {
      console.error(error);

      setErrorMessage("Unexpected error occurred.");
    }
  };

  return (
    <>
      <Dropdown.Item
        onClick={(e) => {
          e.preventDefault();
          handleOpen();
        }}
      >
        <button className="signup-btn">Create Project ...</button>
      </Dropdown.Item>

      <Modal show={showModal} onHide={handleClose} centered size="lg">
        {step === "project" ? (
          <Form onSubmit={handleSubmit}>
            <Modal.Header closeButton>
              <Modal.Title>Create Project</Modal.Title>
            </Modal.Header>

            <Modal.Body>
              {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}

              <Form.Group>
                <Form.Label>Project Name</Form.Label>

                <Form.Control
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Enter project name"
                  required
                />
              </Form.Group>
            </Modal.Body>

            <Modal.Footer>
              <Button variant="secondary" onClick={handleClose}>
                Cancel
              </Button>

              <Button variant="primary" type="submit">
                Create
              </Button>
            </Modal.Footer>
          </Form>
        ) : (
          <>
            {/* <Modal.Header closeButton>
              <Modal.Title>Configure Workflow</Modal.Title>
            </Modal.Header> */}

            <Modal.Body>
              <WorkflowEditor
                projectId={projectId}
                canEdit={true}
                onClose={handleClose}
              />
            </Modal.Body>

            {/* <Modal.Footer>
              <Button variant="success" onClick={handleClose}>
                Finish
              </Button>
            </Modal.Footer> */}
          </>
        )}
      </Modal>
    </>
  );
}

export default CreateProjectPage;
