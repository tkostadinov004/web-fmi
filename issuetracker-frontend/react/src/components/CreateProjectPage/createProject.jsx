import { useState } from "react";
import Dropdown from "react-bootstrap/Dropdown";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Alert from "react-bootstrap/Alert";
import toastr from "../../services/toastrClient";

import axios_client from "../../axiosClient";
import WorkflowEditor from "../TicketDetail/WorkflowEditor";
import handleToastrError from "../../toastrUtils";

function CreateProjectPage({ onProjectCreated }) {
  const [showModal, setShowModal] = useState(false);

  const [step, setStep] = useState("project");
  const [projectId, setProjectId] = useState(null);

  const [form, setForm] = useState({
    name: "",
  });

  const handleOpen = () => {
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);

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
      const createdProject = await axios_client.post(`/projects`, form);

      setProjectId(createdProject.uuid);
      setStep("workflow");
    } catch (error) {
      handleToastrError(error);
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
              <Form.Group>
                <Form.Label>Project Name</Form.Label>

                <Form.Control type="text" name="name" value={form.name} onChange={handleChange} placeholder="Enter project name" required />
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
              <WorkflowEditor projectId={projectId} canEdit={true} onClose={handleClose} />
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
