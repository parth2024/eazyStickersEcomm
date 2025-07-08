import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons";

export default function Footer() {
  return (
    <footer className="flex justify-center items-center py-6 font-primary text-gray-700 dark:text-gray-300 bg-white/80 dark:bg-black/30 backdrop-blur-md">
      Built with
      <FontAwesomeIcon
        icon={faHeart}
        className="text-pink-500 mx-2 animate-pulse drop-shadow-lg"
        aria-hidden="true"
      />
      by{" "}
      <span
        className="ml-2 text-4xl font-black font-serif bg-gradient-to-r from-pink-500 via-fuchsia-500 to-indigo-500 bg-[length:200%_200%] bg-clip-text text-transparent animate-gradient-x drop-shadow-[0_2px_8px_rgba(236,72,153,0.7)] tracking-widest"
        style={{
          letterSpacing: "0.15em",
          textShadow: "0 2px 8px rgba(236,72,153,0.7)",
        }}
      >
        AshCorp
      </span>
      <span className="ml-2 text-lg font-semibold italic text-gray-500 dark:text-gray-400 tracking-wide">
        Ltd
      </span>
      <style jsx>{`
        @keyframes gradient-x {
          0%,
          100% {
            background-position: 0% 50%;
          }
          50% {
            background-position: 100% 50%;
          }
        }
        .animate-gradient-x {
          animation: gradient-x 3s ease-in-out infinite;
        }
      `}</style>
    </footer>
  );
}
