import { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import {
  createSlot,
  generateSlots,
  blockRange,
  listMySlots,
  updateSlot,
  deleteSlot,
} from "../api/availabilityApi";
import { handleApiError } from "../utils/handleApiError";
import { SLOT_STATUS } from "../constants/slotStatus";
import {
  slotDateRules,
  startTimeRules,
  endTimeRules,
  durationMinutesRules,
} from "../validations/availabilityValidation";
import { FaCalendarPlus, FaCalendarAlt, FaClock, FaPlus } from "react-icons/fa";
import { FaLayerGroup, FaStopwatch, FaMagic } from "react-icons/fa";
import { FaBan, FaLock } from "react-icons/fa";

//  filter helpers

function toDateInputValue(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function getTodayDateString() {
  return toDateInputValue(new Date());
}

function getTomorrowDateString() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return toDateInputValue(tomorrow);
}

function getThisWeekRange() {
  const start = new Date();
  const end = new Date();
  end.setDate(end.getDate() + 6);
  return [toDateInputValue(start), toDateInputValue(end)];
}

function getTimeOfDayBucket(timeString) {
  if (!timeString) return null;
  const hour = Number(timeString.split(":")[0]);
  if (Number.isNaN(hour)) return null;
  if (hour < 12) return "morning";
  if (hour < 17) return "afternoon";
  return "evening";
}

const QUICK_DATE_FILTERS = {
  TODAY: "today",
  TOMORROW: "tomorrow",
  THIS_WEEK: "thisWeek",
  ALL: "all",
  CUSTOM: "custom",
};

const DEFAULT_FILTERS = {
  dateMode: QUICK_DATE_FILTERS.TODAY,
  selectedDate: getTodayDateString(),
  status: "all",
  timeOfDay: "all",
  searchTime: "",
};

function DoctorAvailabilityPage() {
  const [slots, setSlots] = useState([]);
  const [loadingSlots, setLoadingSlots] = useState(true);
  const [editingSlotId, setEditingSlotId] = useState(null);
  const [editValues, setEditValues] = useState({
    slot_date: "",
    start_time: "",
    end_time: "",
  });
  const [savingEdit, setSavingEdit] = useState(false);
  const [deletingSlotId, setDeletingSlotId] = useState(null);

  //  filters for the "My Slots" list
  const [dateMode, setDateMode] = useState(DEFAULT_FILTERS.dateMode);
  const [selectedDate, setSelectedDate] = useState(
    DEFAULT_FILTERS.selectedDate,
  );
  const [statusFilter, setStatusFilter] = useState(DEFAULT_FILTERS.status);
  const [timeOfDayFilter, setTimeOfDayFilter] = useState(
    DEFAULT_FILTERS.timeOfDay,
  );
  const [searchTime, setSearchTime] = useState(DEFAULT_FILTERS.searchTime);

  useEffect(() => {
    fetchSlots();
  }, []);

  async function fetchSlots() {
    setLoadingSlots(true);
    try {
      const response = await listMySlots();
      // show soonest slots first
      const sorted = [...response.data].sort((a, b) => {
        if (a.slot_date !== b.slot_date)
          return a.slot_date.localeCompare(b.slot_date);
        return a.start_time.localeCompare(b.start_time);
      });
      setSlots(sorted);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoadingSlots(false);
    }
  }

  //  add a single slot
  const addSlotForm = useForm();
  const [addingSlot, setAddingSlot] = useState(false);

  async function onAddSlot(data) {
    setAddingSlot(true);
    try {
      await createSlot(data);
      toast.success("Slot added");
      addSlotForm.reset();
      await fetchSlots();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setAddingSlot(false);
    }
  }

  //  generate multiple slots for a day
  const generateForm = useForm();
  const [generating, setGenerating] = useState(false);

  async function onGenerateSlots(data) {
    setGenerating(true);
    try {
      const payload = {
        ...data,
        duration_minutes: Number(data.duration_minutes),
      };
      const response = await generateSlots(payload);
      toast.success(
        `${response.data.created_count} slot(s) created, ${response.data.skipped_count} skipped`,
      );
      generateForm.reset();
      await fetchSlots();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setGenerating(false);
    }
  }

  //  block a time range (e.g. for a break or leave)
  const blockForm = useForm();
  const [blocking, setBlocking] = useState(false);

  async function onBlockRange(data) {
    setBlocking(true);
    try {
      const response = await blockRange(data);
      toast.success(
        `${response.data.blocked_count} slot(s) blocked, ${response.data.skipped_booked_count} already booked`,
      );
      blockForm.reset();
      await fetchSlots();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setBlocking(false);
    }
  }

  //  edit an existing available slot
  function startEdit(slot) {
    setEditingSlotId(slot.id);
    setEditValues({
      slot_date: slot.slot_date,
      start_time: slot.start_time,
      end_time: slot.end_time,
    });
  }

  function cancelEdit() {
    setEditingSlotId(null);
  }

  async function saveEdit(slotId) {
    setSavingEdit(true);
    try {
      await updateSlot(slotId, editValues);
      toast.success("Slot updated");
      setEditingSlotId(null);
      await fetchSlots();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setSavingEdit(false);
    }
  }

  async function handleDelete(slotId) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this slot?",
    );
    if (!confirmed) return;
    setDeletingSlotId(slotId);
    try {
      await deleteSlot(slotId);
      toast.success("Slot deleted");
      await fetchSlots();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setDeletingSlotId(null);
    }
  }

  //  filter handlers

  function handleQuickDateFilter(mode) {
    setDateMode(mode);
    if (mode === QUICK_DATE_FILTERS.TODAY) {
      setSelectedDate(getTodayDateString());
    } else if (mode === QUICK_DATE_FILTERS.TOMORROW) {
      setSelectedDate(getTomorrowDateString());
    }
  }

  function handleDatePickerChange(value) {
    setSelectedDate(value);
    setDateMode(QUICK_DATE_FILTERS.CUSTOM);
  }

  function handleClearFilters() {
    setDateMode(DEFAULT_FILTERS.dateMode);
    setSelectedDate(DEFAULT_FILTERS.selectedDate);
    setStatusFilter(DEFAULT_FILTERS.status);
    setTimeOfDayFilter(DEFAULT_FILTERS.timeOfDay);
    setSearchTime(DEFAULT_FILTERS.searchTime);
  }

  //  derive the filtered + still-sorted list of slots
  const filteredSlots = useMemo(() => {
    let result = slots;

    // date filtering
    if (dateMode === QUICK_DATE_FILTERS.THIS_WEEK) {
      const [weekStart, weekEnd] = getThisWeekRange();
      result = result.filter(
        (slot) => slot.slot_date >= weekStart && slot.slot_date <= weekEnd,
      );
    } else if (dateMode !== QUICK_DATE_FILTERS.ALL) {
      result = result.filter((slot) => slot.slot_date === selectedDate);
    }

    // status filtering
    if (statusFilter !== "all") {
      result = result.filter(
        (slot) =>
          String(slot.status).toLowerCase() === statusFilter.toLowerCase(),
      );
    }

    // time of day filtering
    if (timeOfDayFilter !== "all") {
      result = result.filter(
        (slot) => getTimeOfDayBucket(slot.start_time) === timeOfDayFilter,
      );
    }

    // search by start or end time
    const trimmedSearch = searchTime.trim();
    if (trimmedSearch) {
      result = result.filter(
        (slot) =>
          slot.start_time?.includes(trimmedSearch) ||
          slot.end_time?.includes(trimmedSearch),
      );
    }

    return result;
  }, [
    slots,
    dateMode,
    selectedDate,
    statusFilter,
    timeOfDayFilter,
    searchTime,
  ]);

  return (
    <div className="page-container">
      <div className="text-center mb-4">
        <h2 className="fw-bold text-primary">
          <i className="bi bi-calendar2-check me-2"></i>
          Manage Availability
        </h2>
        <p className="text-muted">
          Create, manage and organize your consultation slots.
        </p>
      </div>

      <div className="row g-4 mb-4">
        {/*  Add New Slot  */}
        <div className="col-lg-4 col-md-6">
          <div className="card shadow-sm rounded-4 border border-primary-subtle bg-primary-subtle bg-opacity-25 h-100">
            <div className="card-body p-4">
              <div className="d-flex align-items-center mb-2">
                <div
                  className="rounded-circle bg-white text-primary d-inline-flex align-items-center justify-content-center me-3 shadow-sm"
                  style={{ width: 48, height: 48, fontSize: 20 }}
                >
                  <FaCalendarPlus />
                </div>
                <h5 className="fw-bold mb-0 text-primary">Add New Slot</h5>
              </div>

              <p className="text-muted small mb-3">
                Create a single availability slot for patients to book.
              </p>

              <form
                className="row g-3"
                onSubmit={addSlotForm.handleSubmit(onAddSlot)}
              >
                {/* Date */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaCalendarAlt className="me-2 text-primary" />
                    Date
                  </label>

                  <input
                    type="date"
                    className="form-control"
                    {...addSlotForm.register("slot_date", slotDateRules)}
                  />

                  {addSlotForm.formState.errors.slot_date && (
                    <div className="text-danger small mt-1">
                      {addSlotForm.formState.errors.slot_date.message}
                    </div>
                  )}
                </div>

                {/* Start Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-success" />
                    Start Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...addSlotForm.register("start_time", startTimeRules)}
                  />

                  {addSlotForm.formState.errors.start_time && (
                    <div className="text-danger small mt-1">
                      {addSlotForm.formState.errors.start_time.message}
                    </div>
                  )}
                </div>

                {/* End Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-danger" />
                    End Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...addSlotForm.register("end_time", endTimeRules)}
                  />

                  {addSlotForm.formState.errors.end_time && (
                    <div className="text-danger small mt-1">
                      {addSlotForm.formState.errors.end_time.message}
                    </div>
                  )}
                </div>

                {/* Button */}
                <div className="col-12 d-grid">
                  <button
                    type="submit"
                    className="btn btn-outline-primary"
                    disabled={addingSlot}
                  >
                    <FaPlus className="me-2" />
                    {addingSlot ? "Adding..." : "Add Slot"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>

        {/*  Generate Slots for a Day  */}
        <div className="col-lg-4 col-md-6">
          <div className="card shadow-sm rounded-4 border border-success-subtle bg-success-subtle bg-opacity-25 h-100">
            <div className="card-body p-4">
              <div className="d-flex align-items-center mb-2">
                <div
                  className="rounded-circle bg-white text-success d-inline-flex align-items-center justify-content-center me-3 shadow-sm"
                  style={{ width: 48, height: 48, fontSize: 20 }}
                >
                  <FaLayerGroup />
                </div>
                <h5 className="fw-bold mb-0 text-success">
                  Generate Slots for a Day
                </h5>
              </div>

              <p className="text-muted small mb-3">
                Automatically create multiple slots from a time range and
                duration.
              </p>

              <form
                className="row g-3"
                onSubmit={generateForm.handleSubmit(onGenerateSlots)}
              >
                {/* Date */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaCalendarAlt className="me-2 text-primary" />
                    Date
                  </label>

                  <input
                    type="date"
                    className="form-control"
                    {...generateForm.register("slot_date", slotDateRules)}
                  />

                  {generateForm.formState.errors.slot_date && (
                    <div className="text-danger small mt-1">
                      {generateForm.formState.errors.slot_date.message}
                    </div>
                  )}
                </div>

                {/* Start Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-success" />
                    Start Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...generateForm.register("start_time", startTimeRules)}
                  />

                  {generateForm.formState.errors.start_time && (
                    <div className="text-danger small mt-1">
                      {generateForm.formState.errors.start_time.message}
                    </div>
                  )}
                </div>

                {/* End Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-danger" />
                    End Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...generateForm.register("end_time", endTimeRules)}
                  />

                  {generateForm.formState.errors.end_time && (
                    <div className="text-danger small mt-1">
                      {generateForm.formState.errors.end_time.message}
                    </div>
                  )}
                </div>

                {/* Duration */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaStopwatch className="me-2 text-warning" />
                    Duration (Minutes)
                  </label>

                  <input
                    type="number"
                    className="form-control"
                    placeholder="20"
                    {...generateForm.register(
                      "duration_minutes",
                      durationMinutesRules,
                    )}
                  />

                  {generateForm.formState.errors.duration_minutes && (
                    <div className="text-danger small mt-1">
                      {generateForm.formState.errors.duration_minutes.message}
                    </div>
                  )}
                </div>

                {/* Button */}
                <div className="col-12 d-grid">
                  <button
                    type="submit"
                    className="btn btn-outline-success"
                    disabled={generating}
                  >
                    <FaMagic className="me-2" />
                    {generating ? "Generating..." : "Generate Slots"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>

        {/*  Block Time Range  */}
        <div className="col-lg-4 col-md-6">
          <div
            className="card shadow-sm rounded-4 border border-danger-subtle h-100"
            style={{ backgroundColor: "#fdf0f3" }}
          >
            {" "}
            <div className="card-body p-4">
              <div className="d-flex align-items-center mb-2">
                <div
                  className="rounded-circle bg-white d-inline-flex align-items-center justify-content-center me-3 shadow-sm"
                  style={{
                    width: 48,
                    height: 48,
                    fontSize: 20,
                    color: "#a8557a",
                  }}
                >
                  <FaBan />
                </div>
                <h5 className="fw-bold mb-0" style={{ color: "#a8557a" }}>
                  Block Time Range
                </h5>{" "}
              </div>

              <p className="text-muted small mb-3">
                Block a period like a break . Booked slots stay untouched.
              </p>

              <form
                className="row g-3"
                onSubmit={blockForm.handleSubmit(onBlockRange)}
              >
                {/* Date */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaCalendarAlt className="me-2 text-primary" />
                    Date
                  </label>

                  <input
                    type="date"
                    className="form-control"
                    {...blockForm.register("slot_date", slotDateRules)}
                  />

                  {blockForm.formState.errors.slot_date && (
                    <div className="text-danger small mt-1">
                      {blockForm.formState.errors.slot_date.message}
                    </div>
                  )}
                </div>

                {/* Start Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-success" />
                    Start Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...blockForm.register("start_time", startTimeRules)}
                  />

                  {blockForm.formState.errors.start_time && (
                    <div className="text-danger small mt-1">
                      {blockForm.formState.errors.start_time.message}
                    </div>
                  )}
                </div>

                {/* End Time */}
                <div className="col-12">
                  <label className="form-label fw-semibold small">
                    <FaClock className="me-2 text-danger" />
                    End Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    {...blockForm.register("end_time", endTimeRules)}
                  />

                  {blockForm.formState.errors.end_time && (
                    <div className="text-danger small mt-1">
                      {blockForm.formState.errors.end_time.message}
                    </div>
                  )}
                </div>

                {/* Button */}
                <div>
                  <button
                    type="submit"
                    className="btn btn-outline-danger"
                    disabled={blocking}
                  >
                    <FaLock className="me-2" />
                    {blocking ? "Blocking..." : "Block Range"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      {/* FILTERS + MY SLOTS */}
      <div className="card border-0 shadow-sm rounded-4 mb-4">
        <div className="card-body py-3">
          <h4 className="fw-bold mb-1">
            <i className="bi bi-calendar-check text-primary me-2"></i>
            My Slots
          </h4>
          <small className="text-muted">
            Manage your available appointment slots
          </small>
        </div>
      </div>
      {/* filters for the slots list */}
      <div className="card shadow-sm rounded-4 border-0 bg-light mb-3">
        <div className="card-body">
          <div className="row g-3 align-items-end">
            <div className="col-md-3">
              <label className="form-label">Date</label>
              <input
                type="date"
                className="form-control"
                value={selectedDate}
                onChange={(e) => handleDatePickerChange(e.target.value)}
              />
            </div>

            <div className="col-md-3">
              <label className="form-label">Status</label>
              <select
                className="form-select"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                <option value="all">All</option>
                <option value={SLOT_STATUS.AVAILABLE ?? "available"}>
                  Available
                </option>
                <option value={SLOT_STATUS.BOOKED}>Booked</option>
                <option value={SLOT_STATUS.BLOCKED ?? "blocked"}>
                  Blocked
                </option>
              </select>
            </div>

            <div className="col-md-3">
              <label className="form-label">Time of Day</label>
              <select
                className="form-select"
                value={timeOfDayFilter}
                onChange={(e) => setTimeOfDayFilter(e.target.value)}
              >
                <option value="all">All</option>
                <option value="morning">Morning</option>
                <option value="afternoon">Afternoon</option>
                <option value="evening">Evening</option>
              </select>
            </div>

            <div className="col-md-3">
              <label className="form-label">Search Time</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. 09:30"
                value={searchTime}
                onChange={(e) => setSearchTime(e.target.value)}
              />
            </div>

            <div className="col-md-8">
              <div
                className="btn-group"
                role="group"
                aria-label="Quick date filters"
              >
                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === QUICK_DATE_FILTERS.TODAY
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() =>
                    handleQuickDateFilter(QUICK_DATE_FILTERS.TODAY)
                  }
                >
                  Today
                </button>
                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === QUICK_DATE_FILTERS.TOMORROW
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() =>
                    handleQuickDateFilter(QUICK_DATE_FILTERS.TOMORROW)
                  }
                >
                  Tomorrow
                </button>
                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === QUICK_DATE_FILTERS.THIS_WEEK
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() =>
                    handleQuickDateFilter(QUICK_DATE_FILTERS.THIS_WEEK)
                  }
                >
                  This Week
                </button>
                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === QUICK_DATE_FILTERS.ALL
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() => handleQuickDateFilter(QUICK_DATE_FILTERS.ALL)}
                >
                  All Dates
                </button>
              </div>
            </div>

            <div className="col-md-4 text-md-end">
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary"
                onClick={handleClearFilters}
              >
                Clear Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      <p className="text-muted small">
        Showing {filteredSlots.length} of {slots.length} slots
      </p>

      {loadingSlots && <p>Loading slots...</p>}
      {!loadingSlots && slots.length === 0 && <p>No slots yet.</p>}
      {!loadingSlots && slots.length > 0 && filteredSlots.length === 0 && (
        <p>No slots found. Try adjusting your filters.</p>
      )}

      {!loadingSlots && filteredSlots.length > 0 && (
        <div className="card shadow-sm border-0 rounded-4">
          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th className="ps-4">Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Status</th>
                    <th className="text-center">Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredSlots.map((slot) => {
                    const isEditing = editingSlotId === slot.id;
                    const isBooked = slot.status === SLOT_STATUS.BOOKED;

                    if (isEditing) {
                      return (
                        <tr key={slot.id}>
                          <td className="ps-4">
                            <input
                              type="date"
                              className="form-control form-control-sm"
                              value={editValues.slot_date}
                              onChange={(e) =>
                                setEditValues({
                                  ...editValues,
                                  slot_date: e.target.value,
                                })
                              }
                            />
                          </td>

                          <td>
                            <input
                              type="time"
                              className="form-control form-control-sm"
                              value={editValues.start_time}
                              onChange={(e) =>
                                setEditValues({
                                  ...editValues,
                                  start_time: e.target.value,
                                })
                              }
                            />
                          </td>

                          <td>
                            <input
                              type="time"
                              className="form-control form-control-sm"
                              value={editValues.end_time}
                              onChange={(e) =>
                                setEditValues({
                                  ...editValues,
                                  end_time: e.target.value,
                                })
                              }
                            />
                          </td>

                          <td>
                            <span className="badge bg-light text-dark border">
                              {slot.status}
                            </span>
                          </td>

                          <td className="text-center">
                            <button
                              className="btn btn-outline-success btn-sm me-2"
                              disabled={savingEdit}
                              onClick={() => saveEdit(slot.id)}
                            >
                              Save
                            </button>

                            <button
                              className="btn btn-outline-secondary btn-sm"
                              onClick={cancelEdit}
                            >
                              Cancel
                            </button>
                          </td>
                        </tr>
                      );
                    }

                    return (
                      <tr key={slot.id}>
                        <td className="ps-4 fw-semibold">{slot.slot_date}</td>

                        <td>{slot.start_time}</td>

                        <td>{slot.end_time}</td>

                        <td>
                          {slot.status === SLOT_STATUS.AVAILABLE && (
                            <span className="badge bg-light text-success border px-3 py-2">
                              Available
                            </span>
                          )}

                          {slot.status === SLOT_STATUS.BOOKED && (
                            <span className="badge bg-light text-danger border px-3 py-2">
                              Booked
                            </span>
                          )}

                          {slot.status === SLOT_STATUS.BLOCKED && (
                            <span className="badge bg-light text-warning border px-3 py-2">
                              Blocked
                            </span>
                          )}
                        </td>

                        <td className="text-center">
                          {!isBooked ? (
                            <>
                              <button
                                className="btn btn-outline-primary btn-sm me-2"
                                onClick={() => startEdit(slot)}
                              >
                                Edit
                              </button>

                              <button
                                className="btn btn-outline-danger btn-sm"
                                disabled={deletingSlotId === slot.id}
                                onClick={() => handleDelete(slot.id)}
                              >
                                {deletingSlotId === slot.id
                                  ? "Deleting..."
                                  : "Delete"}
                              </button>
                            </>
                          ) : (
                            <span className="badge bg-light text-secondary border">
                              No Actions
                            </span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default DoctorAvailabilityPage;
