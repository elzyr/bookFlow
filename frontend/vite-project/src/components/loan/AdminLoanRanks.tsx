import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../../utils/fetchWithRefresh";
import {
    BarChart, Bar, PieChart, Pie, Cell, Tooltip, Legend, XAxis, YAxis, CartesianGrid
} from "recharts";
import "../../css/AdminLoanRanks.css"
import { generateLast6Months, formatMonthLabel } from "../../utils/dateUtils";

const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#8dd1e1"];

interface BookLoanRankDto {
    title: string;
    loanCount: number;
}

const AdminLoanRanks = () => {
    const [data, setData] = useState<BookLoanRankDto[]>([]);
    const [averageData, setAverageData] = useState<BookLoanRankDto[]>([]);
    const [chartType, setChartType] = useState<'bar' | 'pie'>('bar');
    const [selectedMonth, setSelectedMonth] = useState<string>(() => {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        return `${year}-${month}`;
    });





    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/loans/ranks", { method: "GET",credentials: "include", })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Błąd HTTP: " + res.status);
                }
                return res.json();

            })
            .then(fetched => {
                setData(fetched)
            })
            .catch(console.error);

        fetchWithRefresh(`http://localhost:8080/loans/averageRanks?fromMonth=${selectedMonth}`, { method: "GET",credentials: "include", })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Błąd HTTP: " + res.status);
                }
                return res.json();
            })
            .then(fetched => setAverageData(fetched))
            .catch(console.error);
    }, [selectedMonth]);

    if(averageData.length < 1){
        return <div><p className="warning-alert">Brak wypożyczeń</p></div>;
    }

    return (
        <div className="page-container">
            <div className="content-card">
                <h2 className="chart-title">Ranking wypożyczeń książek</h2>

                <div className="select-wrapper">
                    <label htmlFor="chartType">Typ wykresu</label>
                    <select
                        id="chartType"
                        value={chartType}
                        onChange={e => setChartType(e.target.value as any)}
                    >
                        <option value="bar">Słupkowy</option>
                        <option value="pie">Kołowy</option>
                    </select>
                </div>

                <div className="chart-container">
                    {chartType === 'bar' ? (
                        <BarChart width={1100} height={500} data={data}>
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis dataKey="title"/>
                            <YAxis allowDecimals={false}/>
                            <Tooltip/>
                            <Legend/>
                            <Bar dataKey="loanCount" name="Wypożyczenia">
                                {data.map((_, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                ))}
                            </Bar>
                        </BarChart>
                    ) : (
                        <PieChart width={1100} height={500}>
                            <Pie
                                data={data}
                                dataKey="loanCount"
                                nameKey="title"
                                outerRadius={180}
                                label
                            >
                                {data.map((_, idx) => (
                                    <Cell key={idx} fill={COLORS[idx % COLORS.length]}/>
                                ))}
                            </Pie>
                            <Tooltip/>
                            <Legend/>
                        </PieChart>
                    )}
                </div>
                <div className="select-wrapper">
                    <label htmlFor="monthSelect">Od którego miesiąca</label>
                    <select
                        id="monthSelect"
                        value={selectedMonth}
                        onChange={(e) => setSelectedMonth(e.target.value)}
                    >
                        {generateLast6Months().map(month => (
                            <option key={month} value={month}>{month}</option>
                        ))}
                    </select>
                </div>
                <div className="average-stats">
                    <h3>Średni czas wypożyczenia (dni) od {formatMonthLabel(selectedMonth)}:</h3>
                    <ul>
                        {averageData.map((item, idx) => (
                            <li key={idx}>
                                <span className="book-title-rank">{item.title}</span>
                                <span className="days"> — {item.loanCount.toFixed(1)} dni</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default AdminLoanRanks;
