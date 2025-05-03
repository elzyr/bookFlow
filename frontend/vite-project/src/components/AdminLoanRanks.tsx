import { useEffect, useState } from "react";
import { fetchWithRefresh } from "../utils/fetchWithRefresh";
import {
    BarChart, Bar, PieChart, Pie, Cell, Tooltip, Legend, XAxis, YAxis, CartesianGrid
} from "recharts";

const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#8dd1e1", "#a4de6c"];

interface BookLoanRankDto {
    title: string;
    loanCount: number;
}

const AdminLoanRanks = () => {
    const [data, setData] = useState<BookLoanRankDto[]>([]);
    const [chartType, setChartType] = useState<'bar' | 'pie'>('bar');

    useEffect(() => {
        fetchWithRefresh("http://localhost:8080/loan/ranks", { method: "GET" })
            .then(res => res.json())
            .then(fetched => setData(fetched))
            .catch(console.error);
    }, []);

    return (
        <div className="p-4 max-w-4xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Ranking wypożyczeń książek</h2>

            <div className="mb-4">
                <label htmlFor="chartType" className="mr-2 font-medium">Typ wykresu:</label>
                <select
                    id="chartType"
                    value={chartType}
                    onChange={e => setChartType(e.target.value as any)}
                    className="border rounded px-2 py-1"
                >
                    <option value="bar">Słupkowy</option>
                    <option value="pie">Kołowy</option>
                </select>
            </div>

            <div style={{ width: '100%', height: 400 }}>
                {chartType === 'bar' ? (
                    <BarChart width={600} height={300} data={data}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="title" />
                        <YAxis allowDecimals={false} />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="loanCount" fill="#8884d8" name="Wypożyczenia" />
                    </BarChart>
                ) : (
                    <PieChart width={600} height={300}>
                        <Pie
                            data={data}
                            dataKey="loanCount"
                            nameKey="title"
                            outerRadius={100}
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
        </div>
    );
};

export default AdminLoanRanks;
