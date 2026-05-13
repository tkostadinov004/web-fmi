import { useState } from "react";

const FILTER_TYPES = [
  { key: "assignee", label: "Assignee" },
  { key: "status", label: "Status" },
];

export const FilterMenu = ({ options, activeFilter, selectedFilters, onFilterTypeChange, onFilterChange }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="filter-menu">
      <button
        className="filter-button"
        onClick={() => setIsOpen((prev) => !prev)}
      >
        Filter
      </button>

      {isOpen && (
        <div className="filter-popup-menu">
          <div className="filter-left">
            {FILTER_TYPES.map(({ key, label }) => (
              <button
                key={key}
                className={`project-button ${activeFilter === key ? "active-project" : ""}`}
                onClick={() => onFilterTypeChange(key)}
              >
                {label}
              </button>
            ))}
          </div>

          <div className="filter-right">
            <div>
              {options.map((option) => (
                <div className="filter-result-row" key={option}>
                  <input
                    type="checkbox"
                    id={`filter-${option}`}
                    value={option}
                    checked={selectedFilters.includes(option)}
                    onChange={() => onFilterChange(option)}
                  />
                  <label htmlFor={`filter-${option}`}>{option}</label>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
