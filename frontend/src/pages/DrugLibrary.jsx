import { useState } from 'react';
import api from '../services/api';

const toList = (value) => {
  if (Array.isArray(value)) return value.filter(Boolean);
  if (typeof value === 'string') {
    return value
      .split(/[,;\n]+/)
      .map((item) => item.trim())
      .filter(Boolean);
  }
  return [];
};

const buildName = (drug, brandNames) => {
  return (
    drug.drugName ||
    drug.name ||
    drug.genericName ||
    (brandNames && brandNames[0]) ||
    'Unknown drug'
  );
};

const DrugLibrary = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openId, setOpenId] = useState(null);

  const dedupeAndFilter = (items, term) => {
    const seen = new Set();
    const cleanTerm = term.trim().toLowerCase();

    return (items || [])
      .map((drug) => {
        const brandNames = toList(drug.brandNames || drug.brandName);
        const primaryName = buildName(drug, brandNames);
        return { ...drug, brandNames, primaryName };
      })
      .filter((drug) => {
        if (!cleanTerm) return true;
        const haystack = `${drug.primaryName} ${drug.brandNames.join(' ')}`.toLowerCase();
        return haystack.includes(cleanTerm);
      })
      .filter((drug) => {
        const key = drug.rxcui || drug.primaryName.toLowerCase();
        if (seen.has(key)) return false;
        seen.add(key);
        return true;
      })
      .sort((a, b) => a.primaryName.localeCompare(b.primaryName));
  };

  const search = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    setError('');
    setResults([]);
    try {
      const res = await api.get('/api/drugs/search', { params: { name: query } });
      setOpenId(null);
      const filtered = dedupeAndFilter(res.data, query);
      setResults(filtered);
      if (filtered.length === 0) {
        setError('No drugs found. Try searching by generic name (e.g., "atorvastatin" instead of "Lipitor").');
      }
    } catch (err) {
      const message = err?.response?.data || err?.message || 'Search failed.';
      setError(typeof message === 'string' ? message : 'Search failed.');
    } finally {
      setLoading(false);
    }
  };

  const renderList = (label, value) => {
    const items = toList(value);
    if (items.length === 0) return null;
    return (
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</p>
        <ul className="mt-1 list-disc space-y-1 pl-5 text-sm text-slate-700">
          {items.map((item, idx) => (
            <li key={`${label}-${idx}`}>{item}</li>
          ))}
        </ul>
      </div>
    );
  };

  const renderText = (label, value) => {
    if (!value) return null;
    return (
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</p>
        <p className="mt-1 text-sm text-slate-700 whitespace-pre-line">{value}</p>
      </div>
    );
  };

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <h1 className="text-2xl font-semibold text-slate-900">Drug Library</h1>
      <p className="text-sm text-slate-600">Search drugs to view indications, warnings, side effects, and dosage forms.</p>

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
          disabled={loading}
          className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
        >
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>

      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}

      <div className="mt-6 space-y-4">
        {results.length === 0 && !loading && (
          <p className="text-sm text-slate-600">No results yet. Try searching for a drug name.</p>
        )}

        {results.map((drug, idx) => {
          const id = drug.rxcui || drug.id || `${drug.primaryName}-${idx}`;
          const isOpen = openId === id;
          const brandNames = drug.brandNames || toList(drug.brandNames || drug.brandName);
          const genericNames = toList(drug.genericName || drug.genericNames || drug.generic);
          const purpose = drug.purpose || drug.summary || drug.description;
          return (
            <div key={id} className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm uppercase tracking-wide text-slate-500">{drug.rxcui ? `RXCUI: ${drug.rxcui}` : 'Drug'}</p>
                  <h2 className="text-lg font-semibold text-slate-900">{drug.primaryName}</h2>
                  {brandNames.length > 0 && (
                    <p className="text-sm text-slate-600">Brand names: {brandNames.join(', ')}</p>
                  )}
                  {genericNames.length > 0 && (
                    <p className="text-sm text-slate-600">Generic names: {genericNames.join(', ')}</p>
                  )}
                </div>
                <button
                  type="button"
                  onClick={() => setOpenId(isOpen ? null : id)}
                  className="rounded border border-slate-200 px-3 py-1 text-xs font-medium text-slate-700 hover:border-slate-300"
                >
                  {isOpen ? 'Hide details' : 'View details'}
                </button>
              </div>

              {isOpen && (
                <div className="mt-4 space-y-4 border-t border-slate-200 pt-4">
                  {renderText('Purpose', purpose)}
                  {renderList('Indications', drug.indications || drug.uses)}
                  {renderList('Warnings', drug.warnings)}
                  {renderList('Side Effects', drug.sideEffects)}
                  {renderList('Dosage Forms', drug.dosageForms || drug.formulations)}
                  {renderList('Other Info', drug.notes || drug.description)}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default DrugLibrary;
