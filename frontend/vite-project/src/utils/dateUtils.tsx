export function generateLast6Months(): string[] {
    const months: string[] = [];
    const now = new Date();

    for (let i = 0; i < 6; i++) {
        const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        months.push(`${year}-${month}`);
    }

    return months.reverse();
}


export const MONTH_NAMES: Record<string, string> = {
    "01": "stycznia",
    "02": "lutego",
    "03": "marca",
    "04": "kwietnia",
    "05": "maja",
    "06": "czerwca",
    "07": "lipca",
    "08": "sierpnia",
    "09": "września",
    "10": "października",
    "11": "listopada",
    "12": "grudnia",
};

export function formatMonthLabel(ym: string): string {
    const [year, month] = ym.split("-");
    return `${MONTH_NAMES[month]} ${year}`;
}