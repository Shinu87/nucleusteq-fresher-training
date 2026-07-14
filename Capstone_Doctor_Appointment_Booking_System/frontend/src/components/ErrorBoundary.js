import { Component } from "react";
import { Link } from "react-router-dom";
import { ROUTES } from "../constants/routes";

// Wrap all routes in an ErrorBoundary so if any page throws an unexpected
// rendering error, the app doesn't crash to a blank screen.

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("Unexpected UI error:", error, errorInfo);
  }

  handleRetry = () => {
    this.setState({ hasError: false });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="page-container text-center">
          <h4>Something went wrong</h4>
          <p className="text-muted">
            This page ran into an unexpected error. Please try again.
          </p>
          <div className="d-flex justify-content-center gap-2">
            <button
              className="btn btn-outline-secondary"
              onClick={this.handleRetry}
            >
              Try Again
            </button>
            <Link to={ROUTES.HOME} className="btn btn-primary">
              Go Home
            </Link>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
