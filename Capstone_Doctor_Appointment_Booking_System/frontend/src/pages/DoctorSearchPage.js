import { useEffect, useState } from "react";
import { searchDoctors } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";
import { SPECIALIZATIONS } from "../constants/specializations";
import DoctorCard from "../components/DoctorCard";

const PAGE_SIZE = 10;

function DoctorSearchPage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [specialization, setSpecialization] = useState("");
  const [minExperience, setMinExperience] = useState("");
  const [maxFee, setMaxFee] = useState("");

  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    setCurrentPage(1);
    fetchDoctors();
  }, [search, specialization, minExperience, maxFee]);

  async function fetchDoctors() {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (search) params.search = search;
      if (specialization) params.specialization = specialization;
      if (minExperience) params.min_experience = minExperience;
      if (maxFee) params.max_fee = maxFee;

      const response = await searchDoctors(params);
      setDoctors(response.data);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  function clearFilters() {
    setSearch("");
    setSpecialization("");
    setMinExperience("");
    setMaxFee("");
  }

  const hasActiveFilters = search || specialization || minExperience || maxFee;

  const totalPages = Math.max(1, Math.ceil(doctors.length / PAGE_SIZE));
  const pagedDoctors = doctors.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE,
  );

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-10">
          {/* Heading */}
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">Find a Doctor</h2>
            <p className="text-muted">
              Search and filter doctors by name, specialization, experience and
              fee.
            </p>
          </div>

          {/* Filter Bar */}
          <div className="card shadow-sm border-0 rounded-4 mb-4">
            <div className="card-body p-4">
              <div className="row g-3 align-items-end">
                <div className="col-md-3">
                  <label htmlFor="doc-search" className="form-label">
                    Name
                  </label>
                  <input
                    id="doc-search"
                    className="form-control"
                    placeholder="Search by name"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                </div>

                <div className="col-md-3">
                  <label htmlFor="doc-spec" className="form-label">
                    Specialization
                  </label>
                  <select
                    id="doc-spec"
                    className="form-select"
                    value={specialization}
                    onChange={(e) => setSpecialization(e.target.value)}
                  >
                    <option value="">All</option>
                    {SPECIALIZATIONS.map((item) => (
                      <option key={item} value={item}>
                        {item}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-md-2">
                  <label htmlFor="doc-exp" className="form-label">
                    Min. experience
                  </label>
                  <input
                    id="doc-exp"
                    type="number"
                    className="form-control"
                    placeholder="Years"
                    value={minExperience}
                    onChange={(e) => setMinExperience(e.target.value)}
                  />
                </div>

                <div className="col-md-2">
                  <label htmlFor="doc-fee" className="form-label">
                    Max. fee
                  </label>
                  <input
                    id="doc-fee"
                    type="number"
                    className="form-control"
                    placeholder="₹"
                    value={maxFee}
                    onChange={(e) => setMaxFee(e.target.value)}
                  />
                </div>

                <div className="col-md-2">
                  <button
                    className="btn btn-outline-secondary w-100"
                    onClick={clearFilters}
                    disabled={!hasActiveFilters}
                  >
                    Clear Filters
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Results Section */}
          {loading && (
            <div className="text-center my-5">
              <p className="text-muted">Loading records…</p>
            </div>
          )}

          {!loading && error && (
            <div className="text-center text-danger my-4">
              <p className="mb-2">{error}</p>
              <button
                className="btn btn-outline-danger btn-sm"
                onClick={fetchDoctors}
              >
                Retry
              </button>
            </div>
          )}

          {!loading && !error && doctors.length === 0 && (
            <div className="text-center my-5">
              <p className="text-muted">No doctors match your filters.</p>
            </div>
          )}

          <div className="row g-4">
            {!loading &&
              !error &&
              pagedDoctors.map((doctor) => (
                <div key={doctor.id} className="col-12 col-md-6 col-lg-4">
                  <div className="card shadow-sm border-0 rounded-4 h-100">
                    <div className="card-body p-4">
                      <DoctorCard doctor={doctor} />
                    </div>
                  </div>
                </div>
              ))}
          </div>

          {!loading && !error && totalPages >= 1 && (
            <div className="d-flex justify-content-center align-items-center gap-3 mt-4">
              <button
                className="btn btn-outline-secondary btn-sm"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage((page) => page - 1)}
              >
                Previous
              </button>
              <span className="text-muted">
                Page {currentPage} of {totalPages}
              </span>
              <button
                className="btn btn-outline-secondary btn-sm"
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage((page) => page + 1)}
              >
                Next
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DoctorSearchPage;
