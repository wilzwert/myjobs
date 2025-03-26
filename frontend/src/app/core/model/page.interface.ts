export interface Page<T> {
    currentPage: number,
    pageSize: number,
    totalElementsCount: number,
    pagesCount: number,
    content: T[]
}