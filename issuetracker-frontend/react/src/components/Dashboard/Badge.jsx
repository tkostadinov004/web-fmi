export const StatusBadge = ({ status }) => {
  const statusClass =
    status === "DONE"
      ? "status-done"
      : status === "IN PROGRESS"
        ? "status-progress"
        : "status-todo";

  return <span className={`status-badge ${statusClass}`}>{status}</span>;
};

export const PriorityBadge = ({ priority }) => (
  <span className="priority-badge">{priority}</span>
);
