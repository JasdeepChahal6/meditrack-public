import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

const MedicationList = () => {
  const [medications, setMedications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notesModal, setNotesModal] = useState({ open: false, content: '' });

  const fetchMedications = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/user-medications/me');
      setMedications(res.data || []);
    } catch (err) {
      const message = err?.response?.data || 'Could not load medications.';
      setError(typeof message === 'string' ? message : 'Could not load medications.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMedications();
  }, []);

  const handleDelete = async (id) => {
    try {
      await api.delete(`/user-medications/${id}`);
      setMedications((prev) => prev.filter((med) => med.id !== id));
    } catch (err) {
      const message = err?.response?.data || 'Delete failed.';
      setError(typeof message === 'string' ? message : 'Delete failed.');
    }
  };

  const truncateNotes = (notes, maxLength = 50) => {
    if (!notes) return null;
    if (notes.length <= maxLength) return notes;
    return notes.substring(0, maxLength) + '...';
  };

  const openNotesModal = (notes) => {
    setNotesModal({ open: true, content: notes });
  };

  const closeNotesModal = () => {
    setNotesModal({ open: false, content: '' });
  };

  if (loading) {
    return <div className="flex h-screen items-center justify-center text-slate-600">Loading...</div>;
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Your Medications</h1>
          <p className="text-sm text-slate-600">Manage your medication list.</p>
        </div>
        <Link
          to="/medications/add"
          className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
        >
          Add Medication
        </Link>
      </div>

      {error && <p className="mb-4 text-sm text-red-600">{error}</p>}

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr>
              <th className="px-4 py-3 text-left font-semibold text-slate-700">Drug</th>
              <th className="px-4 py-3 text-left font-semibold text-slate-700">Dosage</th>
              <th className="px-4 py-3 text-left font-semibold text-slate-700">Frequency</th>
              <th className="px-4 py-3 text-left font-semibold text-slate-700">Start Date</th>
              <th className="px-4 py-3 text-left font-semibold text-slate-700">Notes</th>
              <th className="px-4 py-3 text-right font-semibold text-slate-700">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 bg-white">
            {medications.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-slate-600">
                  No medications found.
                </td>
              </tr>
            )}
            {medications.map((med) => (
              <tr key={med.id}>
                <td className="px-4 py-3 text-slate-900">{med.drugName || <span className="italic text-slate-400">Unknown Drug</span>}</td>
                <td className="px-4 py-3 text-slate-700">{med.dosage}</td>
                <td className="px-4 py-3 text-slate-700">{med.frequency}</td>
                <td className="px-4 py-3 text-slate-700">{med.startDate}</td>
                <td className="px-4 py-3 text-slate-700">
                  {med.instructions ? (
                    <button
                      onClick={() => openNotesModal(med.instructions)}
                      className="text-left text-slate-700 hover:text-slate-900 hover:underline"
                    >
                      {truncateNotes(med.instructions)}
                    </button>
                  ) : (
                    <span className="text-slate-400 italic">No notes</span>
                  )}
                </td>
                <td className="px-4 py-3 text-right">
                  <div className="flex justify-end gap-2">
                    <Link
                      to={`/medications/edit/${med.id}`}
                      className="rounded border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 hover:border-slate-300"
                    >
                      Edit
                    </Link>
                    <button
                      onClick={() => handleDelete(med.id)}
                      className="rounded border border-red-200 px-3 py-1.5 text-xs font-medium text-red-700 hover:border-red-300"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Notes Modal */}
      {notesModal.open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50" onClick={closeNotesModal}>
          <div className="relative mx-4 w-full max-w-lg rounded-lg bg-white p-6 shadow-xl" onClick={(e) => e.stopPropagation()}>
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-semibold text-slate-900">Notes</h3>
              <button
                onClick={closeNotesModal}
                className="rounded p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
              >
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <p className="whitespace-pre-wrap text-sm text-slate-700">{notesModal.content}</p>
            <div className="mt-4 flex justify-end">
              <button
                onClick={closeNotesModal}
                className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MedicationList;
