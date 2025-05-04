import { CategoryInputDto } from "./CategoryInputDto";
import { AuthorInputDto }    from "./AuthorInputDto";

export interface UpdateBookDto {
    title: string;
    yearRelease: number;
    language: string;
    pageCount: number;
    totalCopies: number;
    availableCopies: number;
    description: string;
    categories: CategoryInputDto[];
    authors: AuthorInputDto[];
  }