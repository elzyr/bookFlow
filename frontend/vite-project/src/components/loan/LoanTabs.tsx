type LoanTabsProps = {
    activeTab: "current" | "returned";
    onTabChange: (tab: "current" | "returned") => void;
};

const LoanTabs = ({ activeTab, onTabChange }: LoanTabsProps) => {
    return (
        <div className="loan-btn-group" role="group" aria-label="Zakładki wypożyczeń">
            <button
                type="button"
                className={`btn btn-secondary ${activeTab === "current" ? "active" : ""}`}
                onClick={() => onTabChange("current")}
            >
                Obecnie wypożyczone
            </button>
            <button
                type="button"
                className={`btn btn-secondary ${activeTab === "returned" ? "active" : ""}`}
                onClick={() => onTabChange("returned")}
            >
                Oddane
            </button>
        </div>
    );
};

export default LoanTabs;
