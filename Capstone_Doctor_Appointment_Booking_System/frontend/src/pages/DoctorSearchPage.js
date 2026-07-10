import { useEffect, useState } from "react";
import { searchDoctors } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";
import { SPECIALIZATIONS } from "../constants/specializations";
import DoctorCard from "../components/DoctorCard";
import "../styles/DoctorSearchPage.css";

function DoctorSearchPage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [specialization, setSpecialization] = useState("");
  const [minExperience, setMinExperience] = useState("");
  const [maxFee, setMaxFee] = useState("");

  useEffect(() => {
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
            Clear
          </button>
        </div>
      </div>

      <div className="resultsSection">
        {loading && (
          <p className="statusMessage statusMessageLoading">
            Retrieving records…
          </p>
        )}

        {!loading && error && (
          <p className="statusMessage statusMessageError">{error}</p>
        )}

        {!loading && !error && doctors.length === 0 && (
          <p className="statusMessage">No matching records.</p>
        )}

        {!loading && !error && doctors.length > 0 && (
          <div className="doctorGrid">
            {doctors.map((doctor) => (
              <DoctorCard key={doctor.id} doctor={doctor} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default DoctorSearchPage;
