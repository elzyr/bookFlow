import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh";
import {
    BarChart, Bar, PieChart, Pie, Cell, Tooltip, Legend, XAxis, YAxis, CartesianGrid
} from "recharts";
import "../css/AdminLoanRanks.css"

const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#8dd1e1", "#a4de6c"];

interface BookLoanRankDto {
    title: string;
    loanCount: number;
}

const AdminLoanRanks = () => {
    const [data, setData] = useState<BookLoanRankDto[]>([]);
    const [averageData, setAverageData] = useState<BookLoanRankDto[]>([]);
    const [chartType, setChartType] = useState<'bar' | 'pie'>('bar');

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/loans/ranks", { method: "GET",credentials: "include", })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Błąd HTTP: " + res.status);
                }
                return res.json();
            })
            .then(fetched => setData(fetched))
            .catch(console.error);

        fetchWithRefresh("http://localhost:8080/loans/averageRanks", { method: "GET",credentials: "include", })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Błąd HTTP: " + res.status);
                }
                return res.json();
            })
            .then(fetched => setAverageData(fetched))
            .catch(console.error);
    }, []);

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
                        <BarChart width={800} height={500} data={data}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="title" />
                            <YAxis allowDecimals={false} />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="loanCount" fill="#8884d8" name="Wypożyczenia" />
                        </BarChart>
                    ) : (
                        <PieChart width={800} height={500}>
                            <Pie
                                data={data}
                                dataKey="loanCount"
                                nameKey="title"
                                outerRadius={180}
                                label
                            >
                                {data.map((_, idx) => (
                                    <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip />
                            <Legend />
                        </PieChart>
                    )}
                </div>

                <div className="average-stats">
                    <h3>Średni czas wypożyczenia (dni):</h3>
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
