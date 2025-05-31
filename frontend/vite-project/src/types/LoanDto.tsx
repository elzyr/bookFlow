export enum LoanStatus {
  PENDING_LOAN = "PENDING_LOAN",
  LOAN_ACCEPTED = "LOAN_ACCEPTED",
  PENDING_RETURN = "PENDING_RETURN",
  RETURN_ACCEPTED = "RETURN_ACCEPTED",
}

export interface LoanDto {
  id: number;
  bookId: number;
  title: string;
  borrowDate: string; 
  returnDate: string;
  extendedTime: boolean;
  status: LoanStatus;
  bookReturned: string | null;
  dept: number;
}
