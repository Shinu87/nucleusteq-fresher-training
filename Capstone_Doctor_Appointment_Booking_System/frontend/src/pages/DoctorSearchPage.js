import { useEffect, useState } from "react";
import { searchDoctors } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";
import { SPECIALIZATIONS } from "../constants/specializations";
import DoctorCard from "../components/DoctorCard";
import "../styles/DoctorSearchPage.css";

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
    <div className="pageContainer">
      <header className="pageHeader">
        <h3 className="pageTitle">Find a doctor</h3>
      </header>

      <div className="filterBar">
        <div className="filterField filterFieldWide">
          <label htmlFor="doc-search">Name</label>
          <input
            id="doc-search"
            className="filterInput"
            placeholder="Search by name"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div className="filterField filterFieldWide">
          <label htmlFor="doc-spec">Specialization</label>
          <select
            id="doc-spec"
            className="filterInput"
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

        <div className="filterField">
          <label htmlFor="doc-exp">Min. experience</label>
          <input
            id="doc-exp"
            type="number"
            className="filterInput"
            placeholder="Years"
            value={minExperience}
            onChange={(e) => setMinExperience(e.target.value)}
          />
        </div>

        <div className="filterField">
          <label htmlFor="doc-fee">Max. fee</label>
          <input
            id="doc-fee"
            type="number"
            className="filterInput"
            placeholder="₹"
            value={maxFee}
            onChange={(e) => setMaxFee(e.target.value)}
          />
        </div>

        <div className="filterField filterFieldAction">
          <button
            className="clearButton"
            onClick={clearFilters}
            disabled={!hasActiveFilters}
          >
            Clear Filters
          </button>
        </div>
      </div>

      <div className="resultsSection">
        {loading && (
          <p className="statusMessage statusMessageLoading">Loading records…</p>
        )}

        {!loading && error && (
          <div className="text-danger">
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
          <p className="statusMessage">No doctors match your filters.</p>
        )}

        <div className="row g-4">
          {!loading &&
            !error &&
            pagedDoctors.map((doctor) => (
              <div key={doctor.id} className="col-12 col-md-6 col-lg-4">
                <div className="card shadow-sm border-0 rounded-4 h-100">
                  <div className="card-body">
                    <DoctorCard doctor={doctor} />
                  </div>
                </div>
              </div>
            ))}
        </div>

        {!loading && !error && totalPages >= 1 && (
          <div className="d-flex justify-content-center align-items-center gap-3 mt-3">
            <button
              className="btn btn-outline-secondary btn-sm"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((page) => page - 1)}
            >
              Previous
            </button>
            <span>
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
  );
}

export default DoctorSearchPage;
