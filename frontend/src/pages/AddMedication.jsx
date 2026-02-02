import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const AddMedication = () => {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState({ dosageAmount: '', dosageUnit: 'mg', frequency: '', startDate: '', instructions: '' });
  const [customUnit, setCustomUnit] = useState('');
  const [error, setError] = useState('');
  const [searching, setSearching] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const dosageUnits = [
    'mg', 'mcg', 'g', 'ml', 'L', 'tablet', 'tablets', 'capsule', 'capsules',
    'drop', 'drops', 'spray', 'sprays', 'puff', 'puffs', 'patch', 'IU', 'Other'
  ];

  const search = async (e) => {
    e.preventDefault();
    setSearching(true);
    setError('');
    try {
      const res = await api.get('/api/drugs/search', { params: { name: query } });
      setResults(res.data || []);
      if (!res.data || res.data.length === 0) {
        setError('No drugs found. Try searching by generic name (e.g., "atorvastatin" instead of "Lipitor").');
      }
    } catch (err) {
      // Provide user-friendly error messages
      setError('No drugs found. Try searching by brand name (e.g., "Lipitor") or generic name (e.g., "atorvastatin").');
    } finally {
      setSearching(false);
    }
  };

  const handleFormChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const submit = async (e) => {
    e.preventDefault();
    if (!selected) {
      setError('Select a drug from search results.');
      return;
    }
    
    // Combine dosage amount and unit
    const unit = form.dosageUnit === 'Other' ? customUnit : form.dosageUnit;
    const dosage = `${form.dosageAmount} ${unit}`.trim();
    
    if (!form.dosageAmount || !unit) {
      setError('Please enter both dosage amount and unit.');
      return;
    }
    
    setSubmitting(true);
    setError('');
    try {
      await api.post('/user-medications', {
        drugName: selected.brandName || selected.genericName || selected.name,
        rxcui: selected.rxcui,
        dosage: dosage,
        frequency: form.frequency,
        startDate: form.startDate,
        instructions: form.instructions,
      });
      navigate('/medications');
    } catch (err) {
      const message = err?.response?.data || 'Could not add medication.';
      setError(typeof message === 'string' ? message : 'Could not add medication.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="text-2xl font-semibold text-slate-900">Add Medication</h1>
      <p className="text-sm text-slate-600">Search and select a drug, then add your details.</p>

      <form onSubmit={search} className="mt-4 flex gap-3">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by drug name"
          className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
        />
        <button
          type="submit"
          disabled={searching}
          className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
        >
          {searching ? 'Searching...' : 'Search'}
        </button>
      </form>

      {results.length > 0 && (
        <div className="mt-4 rounded border border-slate-200 bg-white p-4 shadow-sm">
          <h2 className="mb-2 text-sm font-semibold text-slate-900">Results</h2>
          <div className="divide-y divide-slate-200">
            {results.map((item) => (
              <button
                key={item.rxcui || item.drugName || item.name}
                type="button"
                onClick={() => setSelected(item)}
                className={`flex w-full items-start justify-between px-2 py-2 text-left hover:bg-slate-50 ${
                  selected?.rxcui === item.rxcui ? 'bg-slate-100' : ''
                }`}
              >
                <div>
                  <p className="text-sm font-medium text-slate-900">{item.brandName || item.genericName || item.name}</p>
                  {item.genericName && item.brandName && <p className="text-xs text-slate-600">Generic: {item.genericName}</p>}
                </div>
                <span className="text-xs text-slate-500">RXCUI: {item.rxcui || 'N/A'}</span>
              </button>
            ))}
          </div>
        </div>
      )}

      {selected && (
        <form onSubmit={submit} className="mt-6 space-y-4 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <div>
            <p className="text-sm text-slate-600">Selected</p>
            <p className="text-lg font-semibold text-slate-900">{selected.brandName || selected.genericName || selected.name}</p>
            {selected.rxcui && <p className="text-xs text-slate-600">RXCUI: {selected.rxcui}</p>}
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="dosage">Dosage</label>
            <div className="flex gap-2">
              <input
                id="dosageAmount"
                name="dosageAmount"
                type="number"
                step="any"
                value={form.dosageAmount}
                onChange={handleFormChange}
                placeholder="Amount"
                required
                className="w-1/3 rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
              />
              <select
                id="dosageUnit"
                name="dosageUnit"
                value={form.dosageUnit}
                onChange={handleFormChange}
                required
                className="flex-1 rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
              >
                {dosageUnits.map(unit => (
                  <option key={unit} value={unit}>{unit}</option>
                ))}
              </select>
            </div>
            {form.dosageUnit === 'Other' && (
              <input
                type="text"
                value={customUnit}
                onChange={(e) => setCustomUnit(e.target.value)}
                placeholder="Enter custom unit"
                required
                className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
              />
            )}
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="frequency">Frequency</label>
            <input
              id="frequency"
              name="frequency"
              type="text"
              value={form.frequency}
              onChange={handleFormChange}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="startDate">Start Date</label>
            <input
              id="startDate"
              name="startDate"
              type="date"
              value={form.startDate}
              onChange={handleFormChange}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>

          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="instructions">Instructions (optional)</label>
            <textarea
              id="instructions"
              name="instructions"
              value={form.instructions}
              onChange={handleFormChange}
              rows={3}
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>

          {error && <p className="text-sm text-red-600">{error}</p>}

          <button
            type="submit"
            disabled={submitting}
            className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
          >
            {submitting ? 'Saving...' : 'Add Medication'}
          </button>
        </form>
      )}

      {error && !selected && <p className="mt-4 text-sm text-red-600">{error}</p>}
    </div>
  );
};

export default AddMedication;
