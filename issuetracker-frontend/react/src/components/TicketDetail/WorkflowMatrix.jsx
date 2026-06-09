import React from 'react';

const WorkflowMatrix = ({ workflow, activeTransitions, isEditMode, isSaving, onCheckboxChange }) => {
  return (
    <table className="workflow-matrix">
      <thead>
        <tr>
          <th>От \ Към</th>
          {workflow.workflowStatuses.map((status) => (
            <th key={status} className="workflow-th-relative">
              {status.replace(/_/g, ' ')}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {workflow.workflowStatuses.map((rowStatus) => (
          <tr key={rowStatus}>
            <td className="row-header">
              <strong>{rowStatus.replace(/_/g, ' ')}</strong>
            </td>
            
            {workflow.workflowStatuses.map((colStatus) => {
              const isAllowed = activeTransitions.has(`${rowStatus}-${colStatus}`);
              const isSameStatus = rowStatus === colStatus;

              return (
                <td key={colStatus} className="checkbox-cell" title={`Преход от ${rowStatus.replace(/_/g, ' ')} към ${colStatus.replace(/_/g, ' ')}`}>
                  <input
                    type="checkbox"
                    checked={isAllowed}
                    disabled={!isEditMode || isSameStatus || isSaving}
                    onChange={(e) => 
                      onCheckboxChange(rowStatus, colStatus, e.target.checked)
                    }
                  />
                </td>
              );
            })}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default WorkflowMatrix;