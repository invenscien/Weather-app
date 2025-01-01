import React from "react";
import "../styles.css";

function ErrorState({ message }) {
  return (
    <div className="error-state">
      <p>{message}</p>
      <button onClick={() => window.location.reload()}>Retry</button>
    </div>
  );
}

export default ErrorState;
