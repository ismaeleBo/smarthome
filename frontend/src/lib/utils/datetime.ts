import type { CoverageResponse } from '$lib/contracts/measurements';

export type DateTimeRange = {
	from: string; // format for datetime-local: YYYY-MM-DDTHH:mm
	to: string;
};

function pad(value: number): string {
	return String(value).padStart(2, '0');
}

/**
 * Converts an ISO date-time string to the format required by <input type="datetime-local">
 * Example: 2023-01-07T13:18:00 -> 2023-01-07T13:18
 */
export function toDateTimeLocalValue(value: string): string {
	const date = new Date(value);
	if (Number.isNaN(date.getTime())) return '';

	const year = date.getFullYear();
	const month = pad(date.getMonth() + 1);
	const day = pad(date.getDate());
	const hours = pad(date.getHours());
	const minutes = pad(date.getMinutes());

	return `${year}-${month}-${day}T${hours}:${minutes}`;
}

export function fromCoverageToRange(coverage: CoverageResponse): DateTimeRange {
	return {
		from: toDateTimeLocalValue(coverage.from),
		to: toDateTimeLocalValue(coverage.to)
	};
}

export function compareDateTimeLocal(a: string, b: string): number {
	return new Date(a).getTime() - new Date(b).getTime();
}

export function clampDateTimeLocal(value: string, min: string, max: string): string {
	if (!value) return min;
	if (compareDateTimeLocal(value, min) < 0) return min;
	if (compareDateTimeLocal(value, max) > 0) return max;
	return value;
}

export function clampRangeToCoverage(
	range: DateTimeRange | null,
	coverage: CoverageResponse
): DateTimeRange {
	const allowed = fromCoverageToRange(coverage);

	if (!range) return allowed;

	let from = clampDateTimeLocal(range.from, allowed.from, allowed.to);
	let to = clampDateTimeLocal(range.to, allowed.from, allowed.to);

	// ensure from <= to
	if (compareDateTimeLocal(from, to) > 0) {
		from = allowed.from;
		to = allowed.to;
	}

	return { from, to };
}

export function isRangeInsideCoverage(range: DateTimeRange, coverage: CoverageResponse): boolean {
	const allowed = fromCoverageToRange(coverage);

	return (
		compareDateTimeLocal(range.from, allowed.from) >= 0 &&
		compareDateTimeLocal(range.to, allowed.to) <= 0 &&
		compareDateTimeLocal(range.from, range.to) <= 0
	);
}
