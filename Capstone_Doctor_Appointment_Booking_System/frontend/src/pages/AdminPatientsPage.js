import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { listPatients } from "../api/adminApi";
import { handleApiError } from "../utils/handleApiError";
import {
  ACCOUNT_STATUS,
  ACCOUNT_STATUS_OPTIONS,
} from "../constants/accountStatus";
import {
  FaUsers,
  FaCheckCircle,
  FaTimesCircle,
  FaSearch,
  FaArrowLeft,
} from "react-icons/fa";

function AdminPatientsPage() {
  const navigate = useNavigate();
  const [patients, setPatients] = useState([]);
  const [statusFilter, setStatusFilter] = useState("");
  const [genderFilter, setGenderFilter] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPatients();
  }, [statusFilter]);

  async function fetchPatients() {
    setLoading(true);
    try {
      const response = await listPatients(statusFilter);
      setPatients(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  const filteredPatients = patients.filter((patient) => {
    const matchesGender = genderFilter
      ? patient.gender?.toLowerCase() === genderFilter.toLowerCase()
      : true;

    const term = searchTerm.trim().toLowerCase();
    const matchesSearch = term
      ? patient.full_name?.toLowerCase().includes(term) ||
        patient.email?.toLowerCase().includes(term)
      : true;

    return matchesGender && matchesSearch;
  });

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-11">
          {/* Back Button */}
          <button
            className="btn btn-outline-secondary btn-sm mb-3"
            onClick={() => navigate(-1)}
          >
            <FaArrowLeft className="me-2" />
            Back
          </button>

          {/* Heading */}
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">
              <FaUsers className="me-2" />
              Patients
            </h2>
            <p className="text-muted">
              View and manage all registered patients.
            </p>
          </div>

          {/* Filters */}
          <div className="card shadow-sm border-0 rounded-4 mb-4">
            <div className="card-body p-4">
              <div className="row g-3 align-items-end">
                <div className="col-md-4">
                  <label className="form-label fw-bold">
                    Search by name or email
                  </label>
                  <div className="input-group">
                    <span className="input-group-text bg-white">
                      <FaSearch className="text-muted" />
                    </span>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Search patients..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                    />
                  </div>
                </div>

                <div className="col-md-4">
                  <label className="form-label fw-bold">Filter by status</label>
                  <select
                    className="form-select"
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                  >
                    {ACCOUNT_STATUS_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-md-4">
                  <label className="form-label fw-bold">Filter by gender</label>
                  <select
                    className="form-select"
                    value={genderFilter}
                    onChange={(e) => setGenderFilter(e.target.value)}
                  >
                    <option value="">All</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* Loading / Empty States */}
          {loading && (
            <div className="text-center my-5">
              <p className="text-muted">Loading patients...</p>
            </div>
          )}

          {!loading && filteredPatients.length === 0 && (
            <div className="text-center my-5">
              <p className="text-muted">No patients found.</p>
            </div>
          )}

          {/* Patients Table */}
          {!loading && filteredPatients.length > 0 && (
            <div className="card shadow border-0 rounded-4">
              <div className="card-body p-4">
                <div className="table-responsive">
                  <table className="table table-hover align-middle mb-0">
                    <thead>
                      <tr className="text-muted">
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Gender</th>
                        <th>Date of Birth</th>
                        <th>Status</th>
                        <th>Joined</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredPatients.map((patient) => (
                        <tr key={patient.id}>
                          <td className="fw-bold">{patient.full_name}</td>
                          <td>{patient.email}</td>
                          <td>{patient.phone_number}</td>
                          <td>{patient.gender || "-"}</td>
                          <td>{patient.date_of_birth || "-"}</td>
                          <td>
                            {patient.account_status ===
                            ACCOUNT_STATUS.ACTIVE ? (
                              <span className="badge bg-success fs-6">
                                <FaCheckCircle className="me-1" />
                                {patient.account_status}
                              </span>
                            ) : (
                              <span className="badge bg-danger fs-6">
                                <FaTimesCircle className="me-1" />
                                {patient.account_status}
                              </span>
                            )}
                          </td>
                          <td>
                            {new Date(patient.created_at).toLocaleDateString()}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminPatientsPage;
